package examples.chinesechess;

import frame.Game;
import frame.action.Action;
import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.util.Point2D;
import frame.view.View;
import frame.view.board.GridPanelView;
import frame.view.stage.GameStage;

import javax.swing.*;
import java.util.ArrayList;

// 建议在阅读FIR那个例子后再看这个
// 注释之后再写..
// 这是个实现了一部分棋子功能的象棋。总共写了两个小时。

public class Chess {
    public static boolean isSelecting = false;
    public static Piece selectedPiece = null;
    public static ArrayList<Point2D> availablePositions = new ArrayList<>();
    public static Piece.PieceType lastRemovedPieceType;

    public static void main(String[] args) {
        View.window.setSize(1024, 768);
        Game.setMaximumPlayer(2);
        View.setName("Chinese Chess");
        Game.setBoardSize(9, 10);

        Game.registerBoard(Board.class);

        Game.registerGridAction((x, y) -> true, (x, y, mouseButton) -> {
            if (mouseButton == 1) {
                int lastX = 0, lastY = 0;
                if (selectedPiece != null) {
                    lastX = selectedPiece.getX();
                    lastY = selectedPiece.getY();
                }
                int finalLastX = lastX;
                int finalLastY = lastY;
                return new Action(true) {
                    @Override
                    public boolean perform() {
                        if (!isSelecting) {
                            System.out.println("a");
                            BaseGrid grid = Game.getBoard().getGrid(x, y);
                            if (!grid.hasPiece()) return false;
                            Piece piece = (Piece) grid.getOwnedPiece();
                            System.out.println("b");
                            if (piece.getColor() != Color.values()[Game.getCurrentPlayerIndex()]) {
                                return false;
                            }
                            System.out.println("c");
                            availablePositions = piece.getAvailablePositions();
                            selectedPiece = piece;
                            isSelecting = true;
                            EventCenter.publish(new BoardChangeEvent(this));
                            return false;
                        } else {
                            isSelecting = false;
                            for (Point2D point : availablePositions) {
                                System.out.println(point.x + " " + point.y);
                                if (point.x == x && point.y == y) {
                                    BaseGrid grid = Game.getBoard().getGrid(x, y);
                                    if (grid.hasPiece()) {
                                        Piece piece = (Piece) grid.removeOwnedPiece();
                                        lastRemovedPieceType = piece.getType();
                                    }
                                    Game.getBoard().getGrid(selectedPiece.getX(), selectedPiece.getY()).removeOwnedPiece();
                                    selectedPiece.setPosition(x, y);
                                    grid.setOwnedPiece(selectedPiece);
                                    selectedPiece = null;
                                    availablePositions.clear();
                                    return true;
                                }
                            }
                            selectedPiece = null;
                            availablePositions.clear();
                            return false;
                        }
                    }

                    @Override
                    public void undo() {
                        BaseGrid grid = Game.getBoard().getGrid(x, y);
                        Piece piece = (Piece) grid.getOwnedPiece();
                        piece.setPosition(finalLastX, finalLastY);
                    }
                };
            }
            return null;
        });

        Game.setPlayerWinningJudge((player -> {
            if (lastRemovedPieceType == Piece.PieceType.SHUAI
                    && Game.getCurrentPlayerIndex() == player.getId()) {
                return true;
            }
            return false;
        }));

        View.setGridViewPattern(grid -> new GridPanelView() {
            @Override
            public void init() {
                setBackground(java.awt.Color.YELLOW);
            }

            @Override
            public void redraw() {
                boolean isHighLighted = false;
                for (Point2D point : availablePositions) {
                    if (point.x == grid.x && point.y == grid.y) {
                        isHighLighted = true;
                        break;
                    }
                }
                setOpaque(isHighLighted);
                if (grid.hasPiece()) {
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

        View.setPlayerWinView((player -> {
            JOptionPane.showMessageDialog(GameStage.instance(), player.getName() + " Win!");
        }));

        View.start();
    }
}
