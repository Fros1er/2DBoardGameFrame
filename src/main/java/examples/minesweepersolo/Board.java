package examples.minesweepersolo;

import frame.board.BaseBoard;

import java.util.ArrayList;
import java.util.Collections;

public class Board extends BaseBoard {

    public Board(int width, int height) {
        super(width, height);
    }

    @Override
    public void init() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                grids[x][y] = new MineSweeperSolo.myBaseGrid(x, y);
            }
        }
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
                ((MineSweeperSolo.myBaseGrid) grids[x][y]).init(mineArray.get(x + getWidth() * y), number);
            }
        }
    }
}
