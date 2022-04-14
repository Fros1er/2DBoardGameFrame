package frame.board;

import frame.util.Point2D;

import java.util.function.BiConsumer;

public abstract class BaseBoard {

    private final int width, height;
    protected final BaseGrid[][] grids;

    public BaseBoard(int width, int height) {
        this.width = width;
        this.height = height;
        grids = new BaseGrid[width][height];
    }

    public abstract void init();

    public BaseGrid getGrid(int x, int y) {
        return grids[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * move piece from (srcX, srcY) to (destX, destY).
     * @throws ArrayIndexOutOfBoundsException when x or y is out of bounds.
     * @return null if source or destination point doesn't contain piece. Otherwise, return piece in destination.
     */
    public BasePiece movePiece(int srcX, int srcY, int destX, int destY) {
        BasePiece piece = grids[srcX][srcY].removeOwnedPiece();
        if (piece == null) return null;
        piece.setPosition(destX, destY);
        BasePiece res = grids[destX][destY].removeOwnedPiece();
        grids[destX][destY].setOwnedPiece(piece);
        return res;
    }

    /**
     * Performs the given action for each grid in board.
     * @param action The action to be performed for each grid
     */
    public void forEach(BiConsumer<Point2D, BaseGrid> action) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                action.accept(new Point2D(i, j), grids[i][j]);
            }
        }
    }
}
