package frame.board;

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
}
