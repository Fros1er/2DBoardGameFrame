package examples.chinesechess;

import frame.Controller.Game;
import frame.action.Action;
import frame.action.ActionPerformType;
import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.player.PlayerManager;
import frame.util.Point2D;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridPanelView;
import frame.view.components.BackgroundImagePanel;
import frame.view.sound.AudioPlayer;
import frame.view.stage.GameStage;
import frame.view.stage.MenuStage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

// 建议在阅读FIR那个例子后再看这个。
// 这是个实现了一部分棋子功能的象棋，并且加了一些视觉效果。

public class Chess {
    // 全局变量
    public static boolean isSelecting = false; // 当前是否选中棋准备落子
    public static Piece selectedPiece = null; // 选中的棋子
    public static ArrayList<Point2D> availablePositions = new ArrayList<>(); // 所有能走的格子位置
    public static Piece.PieceType lastRemovedPieceType; // 上一个被吃的子

    public static void main(String[] args) {
        View.window.setSize(1024, 768);
        Game.setMaximumPlayer(2);
        View.setName("Chinese Chess");
        Game.setBoardSize(9, 10);
        Game.saver.checkSize(true); // 读档时检查存档棋盘大小
        Game.saver.setSlotNumber(5); // 存档数量

//        AudioPlayer.playBgm("src/main/resources/aaa.mp3"); //播放bgm
//        GameStage.instance().setBgm("src/main/resources/aaa.mp3"); // 在进入GameStage时播放bgm

        Game.registerBoard(Board.class);

        // 基本流程：点一下选中棋子，高亮可以走的格子，然后点高亮的格子落子
        Game.registerGridAction((x, y) -> true, (x, y, mouseButton) -> {
            if (mouseButton == 1) { // 左键
                int lastX = 0, lastY = 0; // 这里到return之前是用来给undo记录坐标的。
                if (selectedPiece != null) { // 如果选中了棋子，就把选中的棋子的坐标存下来。
                    lastX = selectedPiece.getX();
                    lastY = selectedPiece.getY();
                }
                int finalLastX = lastX; // 这里和lambda表达式的捕获有关系。lambda里面用外面的值的时候，会把外面的值复制一份存到里面。
                int finalLastY = lastY; // 复制的时候需要确保外面的变量不会变，所以有这两行。不理解的话抄下来也行。。。
                return new Action(true) {

                    Piece removedPiece = null; // 类中存被吃的棋子，undo的时候放回去。

                    @Override
                    public ActionPerformType perform() {
                        if (!isSelecting) { // 没选中棋子的时候
                            BaseGrid grid = Game.getBoard().getGrid(x, y);
                            if (!grid.hasPiece()) return ActionPerformType.FAIL; // 如果格子上没棋子，Action执行失败
                            Piece piece = (Piece) grid.getOwnedPiece();
                            if (piece.getColor() != Color.values()[Game.getCurrentPlayerIndex()]) {
                                return ActionPerformType.FAIL; // 如果格子上棋子的颜色和玩家颜色不匹配，执行失败
                            }
                            availablePositions = piece.getAvailablePositions(); // 拿所有能走的格子，存到全局变量
                            selectedPiece = piece; // 全局变量存被选中的棋子
                            isSelecting = true;
//                            AudioPlayer.playSound("src/main/resources/bbb.mp3"); //点击音效
                            return ActionPerformType.PENDING; // 执行结果为PENDING，玩家这一步对棋盘没有更改，需要之后的Action
                            // 撤销或者FAIL时会把之前所有的PENDING都撤掉，详见文档
                        } else { // 选中棋子的时候
                            isSelecting = false; // 解除选择
                            for (Point2D point : availablePositions) { // 判断点击的格子是否能走
                                if (point.x == x && point.y == y) {
                                    // 获取被吃掉的棋子，存到Action对象里
                                    this.removedPiece = (Piece) Game.getBoard().movePiece(selectedPiece.getX(), selectedPiece.getY(), x, y);
                                    if (this.removedPiece != null) {
                                        // 如果吃了子，记录最近一个被吃的子的类型（判断被吃的是不是将或者帅）
                                        lastRemovedPieceType = this.removedPiece.getType();
                                    }
                                    selectedPiece = null; // 清理全局变量
                                    availablePositions.clear();
//                                    AudioPlayer.playSound("src/main/resources/ccc.mp3"); //点击音效
                                    return ActionPerformType.SUCCESS; // Action执行成功
                                }
                            }
                            selectedPiece = null; // 清理全局变量
                            availablePositions.clear();
                            EventCenter.publish(new BoardChangeEvent(this));
                            return ActionPerformType.FAIL; // 格子不能走，执行失败
                        }
                    }

                    @Override
                    public void undo() {
                        // 把这一个Action走的棋退回到之前的位置去。
                        // 这里的x和y, finalX和finalY都是之前Action执行的时候复制进来的，不会有改动，所以可以用
                        Game.getBoard().movePiece(x, y, finalLastX, finalLastY);
                        if (removedPiece != null) { // 如果这个Action吃了子，把被吃的子放回去
                            Game.getBoard().getGrid(x, y).setOwnedPiece(removedPiece);
                        }
                    }

                    @Override
                    public void removePending() {
                        // 撤销返回PENDING的Action的时候会调用。
                        // 比如说，刚才高亮的时候记录了全局变量。
                        // 如果是在选中时撤销，由于撤销PENDING的Action不会调用undo，所以需要在这里清理全局变量。
                        selectedPiece = null;
                        availablePositions.clear();
                    }
                };
            }
            return null; // 其他鼠标按键返回null
        });

        // 加一个按钮，可以把兵变成🏇。我也不知道为什么要加这个(
        BackgroundImagePanel sidePanel = new BackgroundImagePanel();
        JButton someButton = new JButton("Promotion");
        someButton.addActionListener((e) -> { // 手动写一个按钮，按下时调用Game.performAction，然后继承一个Action传进去
            Game.performAction(new Action(true) {
                Piece changedPiece = null; // 记录被升变的棋子
                @Override
                public ActionPerformType perform() {
                    if (!isSelecting) return ActionPerformType.FAIL; // 没选中或不是兵返回FAIL
                    if (selectedPiece.getType() != Piece.PieceType.BING) {
                        selectedPiece = null; // 清理全局变量
                        availablePositions.clear();
                        return ActionPerformType.FAIL;
                    }
                    changedPiece = selectedPiece; // 记录改变的棋子，方便撤回
                    selectedPiece.setType(Piece.PieceType.MA); // 改变type
                    selectedPiece = null; // 清理全局变量
                    availablePositions.clear();
                    return ActionPerformType.SUCCESS;
                }

                @Override
                public void undo() {
                    changedPiece.setType(Piece.PieceType.BING); // 把记下来的棋子改回兵
                }
            });
        });
        sidePanel.add(someButton);
        GameStage.instance().add("East", sidePanel); // GameStage的布局管理器是BorderPanel，可以在东西南北添加Panel。框架在南北提供了两个，这里是在东边添加。


        // 胜利条件：刚才被吃的是将/帅，则吃子的玩家赢
        Game.setPlayerWinningJudge((player -> {
            return lastRemovedPieceType == Piece.PieceType.SHUAI
                    && Game.getCurrentPlayerIndex() == player.getId();
        }));

        // 判断游戏结束条件。默认条件是任意一方胜利，但由于和棋规则，这里多判断了当前玩家无棋可走。
        // 判断方式很暴力，遍历了棋盘，找到下一名玩家的所有棋子，判断棋子是不是全都动不了。
        // 这里用的是getNextPlayer，因为游戏结束是在当前玩家回合结束，还没进入下一名玩家的回合时判断。
        Game.setGameEndingJudge(() -> {
            if (PlayerManager.isOnePlayerRemains()) return true; // 先判断是不是有人赢了或者投降
            for (int i = 0; i < Game.getWidth(); i++) { // 遍历棋盘
                for (int j = 0; j < Game.getHeight(); j++) {
                    Grid grid = (Grid) Game.getBoard().getGrid(i, j);
                    if (grid.hasPiece()) {
                        Piece piece = (Piece) grid.getOwnedPiece(); // 如果格子上有子，并且和当前玩家颜色不一样：
                        if (piece.getColor() == Color.values()[Game.getNextPlayerIndex()]) {
                            if (!piece.getAvailablePositions().isEmpty()) { // 判断是不是能走。如果能走则返回false，不平局。
                                return false;
                            }
                        }
                    }
                }
            }
            // 如果都不能走则平局。
            return true;
        });
        try {
            // 设置背景图片。BoardView有个构造函数支持直接设置。其他所有JPanel都是魔改过的，可以直接加图片。
            Image image = ImageIO.read(new File("src/main/resources/bg.jpeg"));
            Image image2 = ImageIO.read(new File("src/main/resources/bg2.jpg"));
            View.setBoardViewPattern(() -> new BoardView(image) {});
            MenuStage.instance().setBackgroundImage(image2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        View.setGridViewPattern(() -> new GridPanelView() {
            boolean isHighLighted = false, hasMouseEntered = false;

            @Override
            public void init() {
                // 这里是鼠标移动到格子上时高亮
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        super.mouseEntered(e);
                        setBackground(new java.awt.Color(255, 255, 150)); //高亮背景色
                        setOpaque(true); // 背景设置为不透明
                        revalidate(); // 这两行建议在改ui之后都加。。
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        super.mouseExited(e);
                        hasMouseEntered = false;
                        if (!isHighLighted) { // 判断是否高亮，如果没高亮就背景透明
                            setOpaque(false);
                        } else { // 高亮的话设回高亮的颜色（黄色）
                            setBackground(java.awt.Color.YELLOW);
                        }
                        revalidate();
                        repaint();
                    }
                });
            }

            @Override
            public void redraw(BaseGrid grid) {
                boolean flag = true;
                for (Point2D point : availablePositions) { // 所有可以走的格子都高亮
                    if (point.x == grid.x && point.y == grid.y) {
                        flag = false;
                        isHighLighted = true;
                        setBackground(java.awt.Color.YELLOW);
                        setOpaque(true);
                        break;
                    }
                }
                if (flag) { // 格子不在可以走的格子里面
                    isHighLighted = false;
                    if (!hasMouseEntered) {
                        setOpaque(false);
                    }
                }
                revalidate();
                repaint();
                if (grid.hasPiece()) { // 绘制棋子，这里直接写文字了。加图片建议用JLabel的Icon。
                    Piece piece = (Piece) grid.getOwnedPiece();
                    this.label.setText(piece.getType().name());
                    if (piece.getColor() == Color.RED)
                        this.label.setForeground(java.awt.Color.RED);
                    else
                        this.label.setForeground(java.awt.Color.BLACK);
                } else {
                    this.label.setText("");
                }
            }
        });

        View.setPlayerWinView((player -> JOptionPane.showMessageDialog(GameStage.instance(), player.getName() + " Win!")));
        View.setPlayerLoseView((player -> JOptionPane.showMessageDialog(GameStage.instance(), player.getName() + " Surrender!")));
        // 设置游戏结束的信息。
        // 由于玩家胜利已经会弹窗了，所以要判断一下是不是平局。
        View.setGameEndView(withdraw -> {
            if (withdraw) {
                JOptionPane.showMessageDialog(GameStage.instance(), "Withdraw!");
            }
        });

        // 在GameStage下面的JPanel显示当前玩家。
        JLabel currentPlayerLabel = new JLabel();
        // 监听BoardChangeEvent。第二个传入的lambda每次接受到BoardChangeEvent都会执行里面的内容。
        EventCenter.subscribe(BoardChangeEvent.class, e -> currentPlayerLabel.setText("Now: " + Color.values()[Game.getCurrentPlayerIndex()].name()));

        // 重置，框架的部分调用Game.init()就行。不过还要重置全局变量。
        JButton reset = new JButton("Reset");
        reset.addActionListener((e) -> {
            isSelecting = false;
            selectedPiece = null;
            availablePositions = new ArrayList<>();
            lastRemovedPieceType = null;
            Game.init();
        });

        // 演示一下Stage文档里提到的自行添加组件
        GameStage.instance().setCustomDrawMethod(() -> {
            GameStage stage = GameStage.instance();
            stage.menuBar.add(reset);
            stage.menuBar.add(stage.menuButton);
            stage.menuBar.add(stage.saveButton);
            stage.menuBar.add(stage.undoButton);
            stage.menuBar.add(stage.surrenderButton);
            stage.scoreBoard.add(currentPlayerLabel);
            stage.add("North", stage.menuBar);
            stage.add("South", stage.scoreBoard);
        });

        View.start();
    }
}
