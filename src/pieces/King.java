package pieces;

import Main.Board;
import Main.Move;

/**
 * The king: moves one square in any direction, and can castle.
 */
public class King extends Piece {
    /**
     * @param board   the board the piece belongs to
     * @param col     starting column
     * @param row     starting row
     * @param isWhite true for a white king, false for a black king
     */
    public King(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "king", 0);
    }

    /**
     * A king moves one square in any direction, or two squares sideways when castling.
     */
    @Override
    public boolean isValidMovement(int col, int row) {
        int dc = Math.abs(col - this.col), dr = Math.abs(row - this.row);
        return (dc != 0 || dr != 0) && dc <= 1 && dr <= 1 || canCastle(col, row);
    }

    /**
     * Checks whether the king may castle to the given square (column 6 = short, column 2 = long).
     *
     * @param col target column
     * @param row target row
     * @return true if castling to that square is allowed
     */
    private boolean canCastle(int col, int row) {
        if (!isFirstMove || this.row != row || this.col != 4) return false;
        if (col == 6) return castleAllowed(7, new int[]{5, 6}, new int[]{4, 5}, row);
        if (col == 2) return castleAllowed(0, new int[]{3, 2, 1}, new int[]{4, 3}, row);
        return false;
    }

    /** rookCol: rook's file; empty: squares that must be empty; safe: king's squares that must not be attacked (start + passed-through) */
    private boolean castleAllowed(int rookCol, int[] empty, int[] safe, int row) {
        Piece rook = board.getPiece(rookCol, row);
        if (rook == null || !rook.name.equals("rook") || rook.isWhite != isWhite || !rook.isFirstMove) return false;
        for (int c : empty) if (board.getPiece(c, row) != null) return false;
        for (int c : safe) if (board.checkScanner.isKingChecked(new Move(board, this, c, row))) return false;
        return true;
    }
}
