package pieces;

import Main.Board;

/**
 * The queen: moves like a rook and a bishop combined.
 */
public class Queen extends Piece {
    /**
     * @param board   the board the piece belongs to
     * @param col     starting column
     * @param row     starting row
     * @param isWhite true for a white queen, false for a black queen
     */
    public Queen(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "queen", 1);
    }

    /**
     * A queen moves along a column, a row or a diagonal.
     */
    @Override public boolean isValidMovement(int col, int row) {
        return this.col == col || this.row == row || Math.abs(col - this.col) == Math.abs(row - this.row);
    }

    /**
     * Checks the path like a rook for straight moves and like a bishop for diagonal moves.
     */
    @Override
    public boolean moveCollidesWithPiece(int col, int row) {
        if (this.col == col || this.row == row) {
            //left
            if (this.col > col)
                for (int c = this.col - 1; c > col; c--)
                    if (board.getPiece(c, this.row) != null)
                        return true;

            //right
            if (this.col < col)
                for (int c = this.col + 1; c < col; c++)
                    if (board.getPiece(c, this.row) != null)
                        return true;

            //up
            if (this.row > row)
                for (int r = this.row - 1; r > row; r--)
                    if (board.getPiece(this.col, r) != null)
                        return true;

            //down
            if (this.row < row)
                for (int r = this.row + 1; r < row; r++)
                    if (board.getPiece(this.col, r) != null)
                        return true;
        }else {
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

        }
        return false;
    }


}
