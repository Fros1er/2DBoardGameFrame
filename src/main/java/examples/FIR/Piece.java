package examples.FIR;

import frame.board.BasePiece;
import frame.player.Player;

public class Piece extends BasePiece {

    private final Color color;
    public Piece(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
