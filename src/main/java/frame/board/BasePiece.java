package frame.board;

import java.io.Serializable;

public abstract class BasePiece implements Serializable {
    protected int x, y;
    public BasePiece(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
