package frame.board;

import frame.action.ActionFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BaseGrid {
    public final int x, y;
    public ActionFactory actionFactory;
    private BasePiece piece;
    public BaseGrid(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public boolean setOwnedPiece(BasePiece piece) {
        if (this.piece != null) return false;
        this.piece = piece;
        return true;
    }

    public BasePiece getOwnedPiece() {
        return piece;
    }

    public BasePiece removeOwnedPiece() {
        BasePiece tmp = piece;
        piece = null;
        return tmp;
    }
}
