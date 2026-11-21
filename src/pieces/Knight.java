package pieces;

import Main.Board;

/**
 * The knight: moves in an L shape (two squares one way, one square the other)
 * and can jump over other pieces.
 */
public class Knight extends Piece {
    /**
     * @param board   the board the piece belongs to
     * @param col     starting column
     * @param row     starting row
     * @param isWhite true for a white knight, false for a black knight
     */
    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board, col, row, isWhite, "knight", 3);

        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
    }

    /**
     * An L-shaped move: the distances in columns and rows multiply to 2.
     */
    @Override public boolean isValidMovement(int col, int row) {
        return Math.abs(col - this.col) * Math.abs(row - this.row) == 2;
    }

    /**
     * A knight jumps, so its path is never blocked.
     */
    @Override public boolean moveCollidesWithPiece(int col, int row) {

        return false;
    }
}
