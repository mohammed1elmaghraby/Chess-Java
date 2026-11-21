package Main;

import pieces.Piece;

/**
 * Describes a single move: which piece moves, where it starts,
 * where it ends and which piece (if any) it captures.
 */
public class Move {
    /** Column the piece moves from. */
    int oldCol;
    /** Row the piece moves from. */
    int oldRow;
    /** Column the piece moves to. */
    int newCol;
    /** Row the piece moves to. */
    int newRow;

    /** The piece that is being moved. */
    Piece piece;
    /** The piece standing on the target square, or null if it is empty. */
    Piece capture;

    /**
     * Creates a move and looks up the piece on the target square.
     *
     * @param board  the board the move is played on
     * @param piece  the piece to move
     * @param newCol target column
     * @param newRow target row
     */
    public Move(Board board, Piece piece, int newCol, int newRow) {
        this.piece = piece;
        this.capture = board.getPiece(newCol, newRow);

        this.oldCol = piece.col;
        this.oldRow = piece.row;
        this.newCol = newCol;
        this.newRow = newRow;
    }
}
