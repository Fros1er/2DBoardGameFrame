package examples.chinesechess;

import frame.board.BaseBoard;

public class Board extends BaseBoard {

    public Board(int width, int height) {
        super(width, height);
    }

    @Override
    public void init() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                grids[i][j] = new Grid(i, j);
            }
        }

        Piece.PieceType bottomLine[] = new Piece.PieceType[]{
                Piece.PieceType.JU,
                Piece.PieceType.MA,
                Piece.PieceType.XIANG,
                Piece.PieceType.SHI,
                Piece.PieceType.SHUAI,
                Piece.PieceType.SHI,
                Piece.PieceType.XIANG,
                Piece.PieceType.MA,
                Piece.PieceType.JU
        };
        for (int i = 0; i < 9; i++) {
            grids[i][0].setOwnedPiece(new Piece(i, 0, bottomLine[i], Color.RED));
        }
        for (int i = 0; i < 9; i++) {
            grids[i][9].setOwnedPiece(new Piece(i, 9, bottomLine[i], Color.BLACK));
        }

        grids[1][2].setOwnedPiece(new Piece(1, 2, Piece.PieceType.PAO, Color.RED));
        grids[7][2].setOwnedPiece(new Piece(7, 2, Piece.PieceType.PAO, Color.RED));
        grids[1][7].setOwnedPiece(new Piece(1, 7, Piece.PieceType.PAO, Color.BLACK));
        grids[7][7].setOwnedPiece(new Piece(7, 7, Piece.PieceType.PAO, Color.BLACK));
        for (int i = 0; i < 9; i += 2) {
            grids[i][3].setOwnedPiece(new Piece(i, 3, Piece.PieceType.BING, Color.RED));
        }
        for (int i = 0; i < 9; i += 2) {
            grids[i][6].setOwnedPiece(new Piece(i, 6, Piece.PieceType.BING, Color.BLACK));
        }
    }
}
