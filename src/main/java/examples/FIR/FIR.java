package examples.FIR;

import frame.Game;
import frame.action.Action;
import frame.player.AIPlayer;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridPanelView;
import frame.view.stage.GameStage;

import javax.swing.*;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.abs;

public class FIR {

    public static int lastChangedX;
    public static int lastChangedY; // 看main里第四条
    public static int[][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
    public static boolean cheating = false; // 作弊模式， 让任意玩家放置任意颜色棋子
    public static Color cheatingColor = Color.BLACK; // 作弊模式放置棋子的颜色

    public static void main(String[] args) {
        // 不建议大家打中文注释，不过在idea里应该不会乱码，而且你们复制代码肯定不会复制注释，所以我就先打着了

        // 1. 游戏的一些杂项设置
        View.window.setSize(600, 400); // 初始窗口大小
        View.setMusic(Objects.requireNonNull(FIR.class.getResource("")).toExternalForm()); // 音乐
        Game.setMaximumPlayer(2); // 最大玩家数
        View.setName("FIR"); //标题栏
        Game.setBoardSize(10, 10);

        // 2.注册棋盘
        /*
         * 有小道消息说你们是黑白棋，正好框架现在也不支持把棋子下在线上，那就凑合一下（
         * 具体的看代码
         * 下面跳到Board.java
         */
        Game.registerBoard(Board.class);

        // 3.注册事件
        // 事件是什么去看文档，这里应该只讲解样例

        Game.registerGridAction((x, y) -> true, (x, y, mouseButton) -> {
            // 这里的lambda表达式如果不写类型名的话IDEA只会把grid认成BaseGrid类
            // 那个 (x, y) -> true代表对每个格子都注册事件
            // 数字的含义看文档

            if (mouseButton == 1) {
                return new Action(!cheating) { // 这里是返回一个继承Action的匿名内部类，传入的boolean代表事件执行成功的话回合结束
                    // 这里还有一些关于作弊模式的判断
                    // 如果开着作弊模式就不结束回合，并且下的棋子颜色由下拉菜单选中的颜色决定
                    @Override
                    public boolean perform() {
                        Grid grid = (Grid) Game.getBoard().getGrid(x, y);
                        if (grid.hasPiece()) return false; // 棋不能下在已经有棋的格子上
                        // 往格子上丢一个新棋子，颜色。。。0是黑1是白，有点不太优雅但我暂时没有更好的办法
                        // getCurrentPlayerIndex代表当前是第几个玩家，这里是直接拿这个index去碰枚举类里Color的顺序了
                        if (cheating) {
                            grid.setOwnedPiece(new Piece(cheatingColor));
                        } else {
                            grid.setOwnedPiece(new Piece(Color.values()[Game.getCurrentPlayerIndex()]));
                        }
                        // setOwnedPiece有个返回值，所以这三行可以合并
                        lastChangedX = x;
                        lastChangedY = y; //看下一条
                        return true;
                    }

                    @Override
                    public void undo() { // 撤销
                        Grid grid = (Grid) Game.getBoard().getGrid(x, y);
                        grid.removeOwnedPiece();
                    }
                };
            }
            return null; // 如果你在检查之后发现你不需要这个按键就返回null
        });

        // 4.判断游戏的输赢
        // 我没想出一个合理的设计能让你们在这个lambda函数里看到上一个被改动的格子，所以在这里有两种解决方式
        // (1) 遍历整个棋盘（棋盘很小，所以不用担心会卡）
        // (2) 开个全局变量记录一下
        // 在这里是用的方法 (2)
        Game.setPlayerWinningJudge((player -> {
            // 推荐使用enhanced for，这里为了照顾没见过的同学就不用了
            for (int i = 0; i < directions.length; i++) { //找八个方向
                boolean flag = true;
                Piece piece = ((Piece) Game.getBoard().getGrid(lastChangedX, lastChangedY).getOwnedPiece());
                // 上一行getOwnedPiece拿到的是BasePiece类，所以你需要强制类型转换。。。
                if (piece == null) return false;
                Color color = piece.getColor();
                for (int j = 1; j < 5; j++) {
                    int x = lastChangedX + directions[i][0] * j, y = lastChangedY + directions[i][1] * j;
                    if (x < 0 || x >= Game.getWidth() || y < 0 || y >= Game.getHeight()) {
                        flag = false;
                        break;
                    }
                    piece = ((Piece) Game.getBoard().getGrid(x, y).getOwnedPiece());
                    if (piece == null || piece.getColor() != color) { // 没有棋或者颜色不对
                        flag = false;
                        break;
                    }
                }
                if (flag) return true; //有一个连成五个就返回
            }
            return false;
        }));

        // 注意，这里判断的是当前回合玩家（在五子棋是里当前落子的玩家）的输赢
        // 所以很显然五子棋里不可能自己下一步棋然后暴毙了
        // 所以return false就完了
        Game.setPlayerLosingJudge((player -> {
            return false;
        }));

        // 游戏结束的判定默认为有一个玩家赢，所以这里不用改
        // 我能想到的常见胜利方式都写在PlayerManager里了，要改的话去调方法
//        Game.setGameEndingJudge();

        // 5.游戏结束时触发的函数
        // 如果你玩过扫雷，你会发现死了之后所有的雷都显示了出来
        // 这里就是用来在游戏结束之后对棋盘/棋子/玩家做操作的
        // 如果要一些视觉效果，看下一条
        // 这个五子棋demo不需要这一步
//        Game.setGameEndFunction(() -> {
//
//        });

        // 6.设置AI
        // 其实AI没那么高大上
        // 比如五子棋， 三个难度的AI可以是：把棋往左上方下，随机下，往自己已有的棋旁边下
        // 虽然很人工智障但是符合了要求
        // 同样的，返回一个匿名内部类
        // AI是一定要有的，不然选择玩家那个界面会有个空的选择AI玩家的框，就非常尴尬
        // 你懒得写直接让这个方法return false（让AI上来就投降）也不是不行。。。
        // French AI（1/1)
        AIPlayer.addAIType("Random", (id) -> {
            return new AIPlayer(id, "Random", 200) {
                @Override
                protected boolean calculateNextMove() {
                    Random random = new Random();
                    for (int i = 0; i < 100; i++) {
                        int x = abs(random.nextInt()) % Game.getWidth();
                        int y = abs(random.nextInt()) % Game.getHeight();
                        if (performGridAction(x, y, 1)) return true;
                    }
                    return false;
                }
            };
        });

        // 6.注册棋盘和格子的样式
        // TODO: 修改棋盘样式
        View.setBoardViewPattern(() -> {
            return new BoardView() {
                @Override
                public void redraw() {}
            };
        });

        //格子的样式
        View.setGridViewPattern((Grid grid) -> { //这个lambda参数也要写类型名
            return new GridPanelView() {
                // 有两种GridView，看文档
                @Override
                public void init() {
                    //初始化
                }

                @Override
                public void redraw() {
                    //这个方法在每次格子有变动的时候都会自己调用一遍
                    if (grid.hasPiece()) {
                        Piece piece = (Piece) grid.getOwnedPiece();
                        if (piece.getColor() == Color.BLACK) this.label.setText("B");
                        else this.label.setText("W");
                    } else {
                        this.label.setText("");
                    }
                }
            };
        });

        // 7.视觉效果
        // 玩家胜利或失败的视觉效果
        View.setPlayerWinView((player -> {
            JOptionPane.showMessageDialog(GameStage.instance(), player.getName() + " Win!");
        }));
        // 由于是实际游戏中弹的框，所以Dialog的parent是GameStage(确信
        // 当然无脑设成View.window也可以

        // 和上面的上面一样的原因，这里不需要
//        View.setPlayerLoseView((player -> {
//
//        }));

        // 游戏结束时的视觉效果
        // 注意这是整场游戏结束，我的设想是用来做一些跳转或者显示返回主菜单按钮的
        View.setGameEndView(() -> {
//            View.changeStage("MenuStage");
        });

        // 8. 自定义菜单栏
        // 看文档，stage里留了一些panel，有需求的话自己往里装
        JLabel cheatText = new JLabel("Cheat mode");
        JCheckBox cheat = new JCheckBox();
        JComboBox<Color> cheatColor = new JComboBox<>();
        cheatColor.addItem(Color.BLACK);
        cheatColor.addItem(Color.WHITE);
        cheat.addActionListener((e) -> {
            cheating = cheat.isSelected();
            cheatColor.setVisible(cheat.isSelected());
        });
        cheatColor.setVisible(false);
        cheatColor.addActionListener((e) -> {
            cheatingColor = (Color) cheatColor.getSelectedItem();
        });

        // 9.开始游戏！
        // 一定要有这一行，里面做了一堆初始化
        View.start();

        //GameStage的菜单栏是在start被执行后创建的，所以如果你想在菜单栏默认按钮后面加，就在start执行后再add你的Component
        GameStage.instance().menuBar.add(cheatText);
        GameStage.instance().menuBar.add(cheat);
        GameStage.instance().menuBar.add(cheatColor);
    }
}