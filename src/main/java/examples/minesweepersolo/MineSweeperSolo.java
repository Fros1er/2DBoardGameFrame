package examples.minesweepersolo;

import frame.Game;
import frame.action.Action;
import frame.board.BaseBoard;
import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridButtonView;
import frame.view.stage.GameStage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class MineSweeperSolo {

    public static int openCount = 0;

    public static void main(String[] args) {

        // ---------------Initialization--------------------------------
        View.window.setSize(600, 400);
        Game.setMaximumPlayer(1);
        View.disableStage("RoomStage", GameStage.instance());

        // ---------------Make Board-------------------------------------
        Game.registerBoard(Board.class);

        //-------------------Set View Of Board--------------------------
        View.setBoardViewPattern(() -> {
            return new BoardView() {
                @Override
                public void redraw() {
                }
            };
        });

        View.setGridViewPattern((myBaseGrid grid) -> {
            return new GridButtonView() {
                @Override
                public void init() {

                }

                @Override
                public void redraw() {
                    if (grid.isOpen) {
                        this.setEnabled(false);
                        if (grid.hasMine) {
                            this.setText("M");
                        } else {
                            this.setText(String.valueOf(grid.getNumber()));
                        }
                    } else {
                        this.setEnabled(true);
                        if (grid.hasFlag) this.setText("F");
                        else this.setText("");
                    }
                }
            };
        });

        // ---------------Register Actions-------------------------------

        Game.registerGridAction((x, y) -> true, (int x, int y, int button) -> {
            switch (button) {
                case 1:
                    return new Action(true) {
                        @Override
                        public boolean perform() {
                            myBaseGrid grid = (myBaseGrid) Game.getBoard().getGrid(x, y);
                            if (grid.isOpen) return false;
                            ArrayList<myBaseGrid> bfs = new ArrayList<>();
                            bfs.add(grid);
                            int now = 0;
                            while (now < bfs.size()) {
                                bfs.get(now).open();
                                if (bfs.get(now).number == 0) {
                                    int xx = bfs.get(now).x;
                                    int yy = bfs.get(now).y;
                                    for (int i = -1; i < 2; i++) {
                                        for (int j = -1; j < 2; j++) {
                                            if (!(i == 0 && j == 0) && xx + i >= 0 && xx + i < Game.getBoard().getWidth() && yy + j >= 0 && yy + j < Game.getBoard().getHeight()) {
                                                myBaseGrid g = (myBaseGrid) Game.getBoard().getGrid(xx + i, yy + j);
                                                if (!g.isOpen) bfs.add(g);
                                            }
                                        }
                                    }
                                }
                                now++;
                            }
                            return true;
                        }

                        @Override
                        public void undo() {

                        }
                    };
                case 3:
                    return new Action(true) {
                        @Override
                        public boolean perform() {
                            myBaseGrid grid = (myBaseGrid) Game.getBoard().getGrid(x, y);
                            grid.reverseFlag();
                            return true;
                        }

                        @Override
                        public void undo() {

                        }
                    };
            }
            return null;
        });

        // --------------------Callbacks--------------------------------

        Game.setGameEndFunction(() -> {
            for (int i = 0; i < Game.getBoard().getWidth(); i++) {
                for (int j = 0; j < Game.getBoard().getHeight(); j++) {
                    myBaseGrid g = (myBaseGrid) Game.getBoard().getGrid(i, j);
                    if (g.hasMine) g.open();
                }
            }
            EventCenter.publish(new BoardChangeEvent(Game.controllerSource));
        });

        // --------------------Game status stuff--------------------------

        Game.setPlayerWinningJudge((player -> {
            int num = 0;
            for (int i = 0; i < Game.getBoard().getWidth(); i++) {
                for (int j = 0; j < Game.getBoard().getHeight(); j++) {
                    myBaseGrid g = (myBaseGrid) Game.getBoard().getGrid(i, j);
                    if (g.hasMine && g.hasFlag) num++;
                }
            }
            return num == 10;
        }));

        Game.setPlayerLosingJudge((player -> {
            for (int i = 0; i < Game.getBoard().getWidth(); i++) {
                for (int j = 0; j < Game.getBoard().getHeight(); j++) {
                    myBaseGrid g = (myBaseGrid) Game.getBoard().getGrid(i, j);
                    if (g.hasMine && g.isOpen) return true;
                }
            }
            return false;
        }));

        // ------------------Winning and Losing Views--------------------------
        View.setPlayerWinView((player -> {
            JOptionPane.showMessageDialog(GameStage.instance(), "You Win!");
        }));
        View.setPlayerLoseView((player -> {
            JOptionPane.showMessageDialog(GameStage.instance(), "You Lose.");
        }));
        View.setGameEndView(() -> {
//            View.changeStage("MenuStage");
        });

        // ------------------------------Menu bar------------------------------------
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener((e -> Game.init()));
        GameStage.instance().menuBar.add(resetButton);

        JButton backButton = new JButton("Back to menu");
        backButton.addActionListener(e -> View.changeStage("MenuStage"));
        GameStage.instance().menuBar.add(backButton);

        View.setName("Minesweeper");
        View.start();
    }



    public static class myBaseGrid extends BaseGrid {

        public boolean hasMine;
        public boolean isOpen;
        public boolean hasFlag;
        private int number;

        public myBaseGrid(int x, int y) {
            super(x, y);
        }

        public void reverseFlag() {
            hasFlag = !hasFlag;
        }

        public boolean open() {
            if (isOpen) return false;
            isOpen = true;
            return true;
        }

        public int getNumber() {
            return number;
        }

        public void init(boolean hasMine, int number) {
            this.hasMine = hasMine;
            if (hasMine) this.number = -1;
            else this.number = number;
            this.isOpen = false;
            this.hasFlag = false;
        }
    }
}