package examples.minesweepersolo;

import frame.Controller.Game;
import frame.action.Action;
import frame.action.ActionPerformType;
import frame.board.BaseGrid;
import frame.event.BoardChangeEvent;
import frame.event.EventCenter;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridPanelView;
import frame.view.stage.GameStage;

import javax.swing.*;
import java.util.ArrayList;

public class MineSweeperSolo {

    public static int openCount = 0;

    public static void main(String[] args) {

        // This example is not working well.
        // Example FIR is recommended.

        // ---------------Initialization--------------------------------
        View.window.setSize(600, 400);
        Game.setMaximumPlayer(1);
        View.setName("Minesweeper");
        Game.setBoardSize(10, 10);

        // ---------------Make Board-------------------------------------
        Game.registerBoard(Board.class);

        //-------------------Set View Of Board--------------------------
        View.setBoardViewPattern(() -> new BoardView() {
            @Override
            public void redraw() {
            }
        });

        View.setGridViewPattern(() -> new GridPanelView() {
            @Override
            public void init() {

            }

            @Override
            public void redraw(BaseGrid raw) {
                myBaseGrid grid = (myBaseGrid) raw;
                if (grid.isOpen) {
//                    this.setEnabled(false);
                    if (grid.hasMine) {
                        this.label.setText("M");
                    } else {
                        this.label.setText(String.valueOf(grid.getNumber()));
                    }
                } else {
//                    this.setEnabled(true);
                    if (grid.hasFlag) this.label.setText("F");
                    else this.label.setText("");
                }
            }
        });

        // ---------------Register Actions-------------------------------

        Game.registerGridAction((x, y) -> true, (int x, int y, int button) -> {
            switch (button) {
                case 1:
                    return new Action(true) {
                        @Override
                        public ActionPerformType perform() {
                            myBaseGrid grid = (myBaseGrid) Game.getBoard().getGrid(x, y);
                            if (grid.isOpen) return ActionPerformType.FAIL;
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
                            return ActionPerformType.SUCCESS;
                        }

                        @Override
                        public void undo() {

                        }
                    };
                case 3:
                    return new Action(true) {
                        @Override
                        public ActionPerformType perform() {
                            myBaseGrid grid = (myBaseGrid) Game.getBoard().getGrid(x, y);
                            grid.reverseFlag();
                            return ActionPerformType.SUCCESS;
                        }

                        @Override
                        public void undo() {

                        }
                    };
            }
            return null;
        });

        // --------------------Callbacks--------------------------------

        Game.setGameEndFunction((withdraw) -> {
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

        Game.setGameEndingJudge((() -> {
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

        // ------------------------------Menu bar------------------------------------
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener((e -> Game.init()));
        GameStage.instance().menuBar.add(resetButton);

        JButton backButton = new JButton("Back to menu");
        backButton.addActionListener(e -> View.changeStage("MenuStage"));
        GameStage.instance().menuBar.add(backButton);


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