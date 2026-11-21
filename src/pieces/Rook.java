package pieces;

import Main.Board;

/**
 * The rook: moves any number of squares along a column or a row.
 */
public class Rook extends Piece {
    /**
     * @param board   the board the piece belongs to
     * @param col     starting column
     * @param row     starting row
     * @param isWhite true for a white rook, false for a black rook
     */
    public Rook(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "rook", 4);
    }

    /**
     * A rook moves along a column or a row.
     */
    @Override public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row;
    }

    /**
     * Checks every square between the rook and the target in the direction of the move.
     */
    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        //left
        if (this.col > col)
            for (int c = this.col -1; c > col; c--)
                if (board.getPiece(c, this.row) != null)
                    return true;

        //right
        if (this.col < col)
            for (int c = this.col +1; c < col; c++)
                if (board.getPiece(c, this.row) != null)
                    return true;

        //up
        if (this.row > row)
            for (int r = this.row -1; r > row; r--)
                if (board.getPiece(this.col, r) != null)
                    return true;

        //down
        if (this.row < row)
            for (int r = this.row +1; r < row; r++)
                if (board.getPiece(this.col, r) != null)
                    return true;

        return false;
    }
}
