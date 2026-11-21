package pieces;

import Main.Board;

/**
 * The bishop: moves any number of squares along a diagonal.
 */
public class Bishop extends Piece {
    /**
     * @param board   the board the piece belongs to
     * @param col     starting column
     * @param row     starting row
     * @param isWhite true for a white bishop, false for a black bishop
     */
    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "bishop", 2);
    }

    /**
     * A bishop moves along a diagonal (the same distance in columns and rows).
     */
    @Override
    public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) == Math.abs(row - this.row);
    }

    /**
     * Checks every square between the bishop and the target along the diagonal.
     */
    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        //up-left
        if (this.col > col && this.row > row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col-i, this.row-i) != null) return true;

        //up-right
        if (this.col < col && this.row > row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col+i, this.row-i) != null) return true;

        //down-left
        if (this.col > col && this.row < row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col-i, this.row+i) != null) return true;

        //down-right
        if (this.col < col && this.row < row)
            for (int i = 1; i < Math.abs(this.col - col); i++)
                if (board.getPiece(this.col+i, this.row+i) != null) return true;

        return false;
    }
}
