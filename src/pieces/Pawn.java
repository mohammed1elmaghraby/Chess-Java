package pieces;

import Main.Board;

/**
 * The pawn: pushes forward one square (two on its first move), captures diagonally,
 * and can capture en passant.
 */
public class Pawn extends Piece {
    /**
     * @param board   the board the piece belongs to
     * @param col     starting column
     * @param row     starting row
     * @param isWhite true for a white pawn, false for a black pawn
     */
    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "pawn", 5);
    }

    /**
     * Checks the pawn's moves: one-square push, two-square push on the first move,
     * diagonal capture, and en passant.
     */
    @Override public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite ? 1 : -1;

        //push pawn 1
        if (this.col == col && row == this.row - colorIndex && board.getPiece(col, row) == null) return true;

        //push pawn 2
        if (isFirstMove
                && this.col == col
                && row == this.row - colorIndex*2
                && board.getPiece(col, row) == null
                && board.getPiece(col, row+colorIndex) == null) return true;

        //capture left
        if (col == this.col-1 && row == this.row-colorIndex && board.getPiece(col, row) != null) return true;

        //capture right
        if (col == this.col+1 && row == this.row-colorIndex && board.getPiece(col, row) != null) return true;

        //en passant (diagonal step onto the en passant tile, beside an enemy pawn)
        if (board.getTileNum(col, row) == board.enPassantTile
                && Math.abs(col - this.col) == 1 && row == this.row - colorIndex) {
            Piece victim = board.getPiece(col, row + colorIndex);
            if (victim != null && victim.name.equals("pawn") && victim.isWhite != this.isWhite) return true;
        }

        return false;
    }
}
