package frame.board;

public abstract class Board {

    private final int width, height;
    protected final Grid[][] grids;

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        grids = new Grid[width][height];
    }

    public abstract void init();

    public Grid getGrid(int x, int y) {
        return grids[x][y];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
