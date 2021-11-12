import frame.Game;
import frame.action.Action;
import frame.board.Board;
import frame.board.Grid;
import frame.view.View;
import frame.view.board.BoardView;
import frame.view.board.GridButtonView;
import frame.view.stage.GameStage;
import frame.view.stage.MenuStage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Main {

    public static int openCount = 0;

    public static void main(String[] args) {
        //uh, frame is static and all scenes are singleton...?
        //Frame f = new Frame(); //With default menu, room, load&save, game and setting stages, using cardView

        // ---------------Initialization--------------------------------
        View.window.setSize(600, 400);
        View.setMusic(Objects.requireNonNull(Main.class.getResource("")).toExternalForm());
        Game.setMaximumPlayer(4);
        Game.setAIStatus(true); //enable or disable AI
        Game.setOnlineStatus(true); //enable or disable Web
        View.disableStage("RoomStage", GameStage.instance());

        // ---------------Make Board-------------------------------------
        Game.registerBoard(new Board(10, 10) {
            @Override
            public void init() {
                ArrayList<Boolean> mineArray = new ArrayList<>(100);
                for (int i = 0; i < getWidth() * getHeight(); i++) {
                    if (i < 10) mineArray.add(true);
                    else mineArray.add(false);
                }
                Collections.shuffle(mineArray);
                for (int x = 0; x < getWidth(); x++) {
                    for (int y = 0; y < getHeight(); y++) {
                        int number = 0;
                        for (int i = -1; i < 2; i++) {
                            for (int j = -1; j < 2; j++) {
                                if (!(i == 0 && j == 0) && x + i >= 0 && x + i < getWidth() && y + j >= 0 && y + j < getHeight()) {
                                    if (mineArray.get((x+i) + getWidth() * (y+j))) number++;
                                }
                            }
                        }
                        grids[x][y] = new myGrid(x, y, mineArray.get(x + getWidth() * y), number);
                    }
                }
            }
        });

        //-------------------Set View Of Board--------------------------
        View.setBoardViewPattern(() -> {
            return new BoardView(10, 10) {
                @Override
                public void redraw() {
                }
            };
        });

        View.setGridViewPattern((myGrid grid) -> {
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
                        if (grid.hasFlag) this.setText("F");
                        else this.setText("");
                    }
                }
            };
        });

        // ---------------Register Actions-------------------------------

        Game.registerGridAction((x, y) -> true, (myGrid grid, int x, int y, int button) -> {
            switch (button) {
                case 1:
                    return new Action(true) {
                        @Override
                        public boolean perform() {
                            if (grid.isOpen) return false;
                            ArrayList<myGrid> bfs = new ArrayList<>();
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
                                                myGrid g = (myGrid) Game.getBoard().getGrid(xx + i, yy + j);
                                                if (!g.isOpen) bfs.add(g);
                                            }
                                        }
                                    }
                                }
                                now++;
                            }
                            return true;
                        }
                    };
                case 3:
                    return new Action(true) {
                        @Override
                        public boolean perform() {
                            grid.reverseFlag();
                            return true;
                        }
                    };
            }
            return null;
        });

        // --------------------Game status stuff--------------------------

        Game.setPlayerWinningFunction((player -> {
            int num = 0;
            for (int i = 0; i < Game.getBoard().getWidth(); i++) {
                for (int j = 0; j < Game.getBoard().getHeight(); j++) {
                    myGrid g = (myGrid) Game.getBoard().getGrid(i, j);
                    if (g.hasMine && g.hasFlag) num++;
                }
            }
            return num == 10;
        }));

        Game.setPlayerLosingFunction((player -> {
            for (int i = 0; i < Game.getBoard().getWidth(); i++) {
                for (int j = 0; j < Game.getBoard().getHeight(); j++) {
                    myGrid g = (myGrid) Game.getBoard().getGrid(i, j);
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
            View.changeStage("MenuStage");
        });



        /* ----------- Input Handling -----------------------------------------
            Used to handle mouse events except in board and keyboard events.
            Can be global or limited in a certain stage (or a component in a stage, depending on whether I can implement it or not).
        */

//        Frame.window.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                System.out.println("click global");
//            }
//        });


        // ---------- ~Input Handling -----------------------

        // We need more things

        //Frame.disableStage("RoomStage"); //stage is map<String, Stage>, should be able to enable and disable (Maybe?)
        /*
            Custom stage: stage is like a JFrame. Just add sth.
        */
        //Frame.addStage("License", CustomStage().Instance()); //should be able to set custom stage
        //Frame.getStage("Custom");


        /* -------- menu stage ----------------
        by default it should be like:

                    G A M E N A M E
                            New Game          // jump to roomStage or gameStage, depending on setting
                            Load              // jump to load&save stage
                            Settings          // jump to setting stage
                            quit              // quit game

        */
        MenuStage m = MenuStage.instance();
        View.setName("Minesweeper");

        View.start();
    }

    public static class myGrid extends Grid {

        public final boolean hasMine;
        public boolean isOpen = false;
        public boolean hasFlag = false;
        private final int number;

        public myGrid(int x, int y, boolean hasMine, int number) {
            super(x, y);
            this.hasMine = hasMine;
            if (hasMine) this.number = 0;
            else this.number = number;
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
    }
}