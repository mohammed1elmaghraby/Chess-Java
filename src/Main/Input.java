package Main;

import pieces.Piece;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Handles the mouse: selecting a piece, dragging it and dropping it on a square.
 */
public class Input extends MouseAdapter {
    /** The board this listener controls. */
    Board board;
    /**
     * Selects the piece under the mouse when a button is pressed.
     */
    @Override
    public void mousePressed(MouseEvent e) {

        int col = e.getX()/board.tileSize;
        int row = e.getY()/board.tileSize;

        Piece pieceXY = board.getPiece(col, row);
        if (pieceXY != null) board.selectedPiece = pieceXY;
    }

    /**
     * Drops the selected piece. A legal move is played;
     * an illegal move sends the piece back to its square.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        int col = e.getX() / board.tileSize;
        int row = e.getY() / board.tileSize;

        if (board.selectedPiece != null) {
            Move move = new Move(board, board.selectedPiece, col, row);
            if (board.isValidMove(move)) {
                board.makeMove(move);
            } else {
                // snap back to its square
                board.selectedPiece.xPos = board.selectedPiece.col * board.tileSize;
                board.selectedPiece.yPos = board.selectedPiece.row * board.tileSize;
            }
            board.selectedPiece = null;
            board.repaint();
        }
    }

    /**
     * Moves the selected piece with the mouse while it is being dragged.
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (board.selectedPiece != null) {
            board.selectedPiece.xPos = e.getX() - board.tileSize/2;
            board.selectedPiece.yPos = e.getY() - board.tileSize/2;
        }

        board.repaint();

    }


    /**
     * @param board the board to control
     */
    public Input(Board board) {
        this.board = board;
    }
}
