package Main;

import pieces.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * The chess board. It stores the pieces, decides which moves are legal,
 * plays the moves and draws everything on the screen.
 */
public class Board extends JPanel {

    /** Number of columns of the board. */
    int cols = 8;
    /** Number of rows of the board. */
    int rows = 8;
    /** Width and height of one square, in pixels. */
    public int tileSize = 85;

    /** All pieces that are still on the board. */
    ArrayList<Piece> piecesList = new ArrayList<>();
    /** The piece the player is currently dragging, or null. */
    public Piece selectedPiece;

    /** Mouse listener that lets the player move the pieces. */
    Input input = new Input(this);

    /** Used to detect check, checkmate and stalemate. */
    public CheckScanner checkScanner = new CheckScanner(this);

    /** Tile number the en passant capture lands on, or -1 if en passant is not possible. */
    public int enPassantTile = -1;

    /** True while it is white's turn. */
    private boolean isWhitetoMove = true;
    /** True once the game has ended; no more moves are accepted. */
    private boolean isGameOver = false;

    /**
     * Creates the board, sets its size, registers the mouse listener and places the pieces.
     */
    public Board() {
        this.setPreferredSize(new Dimension(cols*tileSize, rows*tileSize));

        this.addMouseListener(input);
        this.addMouseMotionListener(input);

        addPieces();
    }

    /**
     * Finds the piece on a square.
     *
     * @param col column of the square
     * @param row row of the square
     * @return the piece on that square, or null if it is empty
     */
    public Piece getPiece(int col, int row) {
        for (Piece piece : piecesList) {
            if (piece.col == col && piece.row == row) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Checks whether a move is legal: it must be the piece's turn, the game must be running,
     * the target must be on the board and not hold a friendly piece, the piece must be able
     * to move that way without jumping over others, and the own king must not end in check.
     *
     * @param move the move to test
     * @return true if the move is legal
     */
    public boolean isValidMove(Move move) {
        if (move.piece.isWhite != isWhitetoMove) return false;
        if (isGameOver) return false;
        if (move == null || move.piece == null) return false;
        if (move.newCol < 0 || move.newCol >= cols || move.newRow < 0 || move.newRow >= rows) return false;
        if (sameTeam(move.piece, move.capture)) return false;
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) return false;
        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) return false;
        if (checkScanner.isKingChecked(move)) return false;

        return true;
    }

    /**
     * @return true if both pieces exist and have the same colour
     */
    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) return false;
        return p1.isWhite == p2.isWhite;
    }

    /**
     * Plays a move that is already known to be legal: handles the special pawn and king moves,
     * moves the piece, removes the captured piece, passes the turn and checks if the game ended.
     *
     * @param move the legal move to play
     */
    public void makeMove(Move move) {
        if (move.piece.name.equals("pawn")) {
            movePawn(move);
        }else if (move.piece.name.equals("king")) {
            moveKing(move);
        }

        move.piece.col = move.newCol;
        move.piece.row = move.newRow;
        move.piece.xPos = move.newCol * tileSize;
        move.piece.yPos = move.newRow * tileSize;

        move.piece.isFirstMove = false;
        enPassantTile = -1;   // en passant is only possible on the very next move

        capture(move.capture);

        isWhitetoMove = !isWhitetoMove;

        updateGameState();

    }

    /**
     * Handles castling: when the king moves two squares, the matching rook is moved as well.
     *
     * @param move the king's move
     */
    private void moveKing(Move move) {
        if (Math.abs(move.piece.col - move.newCol) == 2) {
            Piece rook;
            if (move.piece.col < move.newCol) {
                rook = getPiece(7, move.piece.row);
                rook.col = 5;
            }else {
                rook = getPiece(0, move.piece.row);
                rook.col = 3;
            }
            rook.xPos = rook.col*tileSize;
            rook.isFirstMove = false;
        }

    }

    /**
     * Handles the special pawn moves: en passant capture, remembering the en passant tile
     * after a two-square push, and promotion on the last rank.
     *
     * @param move the pawn's move
     */
    private void movePawn(Move move) {
        //en passant
        int colorIndex = move.piece.isWhite? 1 : -1;
        if (getTileNum(move.newCol, move.newRow) == enPassantTile && move.newCol != move.piece.col) {
            move.capture = getPiece(move.newCol, move.newRow + colorIndex);
        }

        if (Math.abs(move.piece.row - move.newRow) == 2) {
            enPassantTile = getTileNum(move.newCol, move.newRow + colorIndex);
        }else {
            enPassantTile = -1;
        }

        // promotions
        colorIndex = move.piece.isWhite? 0 : 7;
        if (move.newRow == colorIndex) {
            promotePawn(move);
        }
    }

    /**
     * Promotes a pawn that has reached the last rank.
     * Asks the player which piece they want, then replaces the pawn with it.
     *
     * @param move the move that brings the pawn to the last rank
     */
    private void promotePawn(Move move) {
        // Clear the selection so no move highlights are drawn while the dialog is open
        selectedPiece = null;

        Piece promoted = createPromotionPiece(move.newCol, move.newRow, move.piece.isWhite);

        piecesList.add(promoted);
        capture(move.piece);   // remove the pawn from the board
    }

    /**
     * Shows a dialog that lets the player choose the promotion piece.
     * If the dialog is closed without a choice, the pawn becomes a queen.
     *
     * @param col     column of the promotion square
     * @param row     row of the promotion square
     * @param isWhite true if the promoted piece is white
     * @return the newly created piece
     */
    private Piece createPromotionPiece(int col, int row, boolean isWhite) {
        String[] options = {"Queen", "Rook", "Bishop", "Knight"};

        int choice = JOptionPane.showOptionDialog(
                this,
                "Choose a piece to promote your pawn to:",
                "Pawn Promotion",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        switch (choice) {
            case 1:  return new Rook(this, col, row, isWhite);
            case 2:  return new Bishop(this, col, row, isWhite);
            case 3:  return new Knight(this, col, row, isWhite);
            default: return new Queen(this, col, row, isWhite);   // Queen, or dialog closed
        }
    }

    /**
     * Removes a piece from the board.
     *
     * @param piece the piece to remove
     */
    public void capture(Piece piece) {
        piecesList.remove(piece);
    }

    /**
     * Converts a column and a row into one tile number (0 to 63).
     *
     * @param col column of the square
     * @param row row of the square
     * @return the tile number
     */
    public int getTileNum(int col, int row) {
        return row * rows + col;
    }

    /**
     * Finds the king of one side.
     *
     * @param isWhite true for the white king, false for the black king
     * @return the king, or null if it is not on the board
     */
    Piece findKing(boolean isWhite) {
        for (Piece piece : piecesList) {
            if(isWhite == piece.isWhite && piece.name.equals("king")) return piece;
        }

        return null;
    }

    /**
     * Puts the pieces on their starting squares.
     */
    public void addPieces() {
       piecesList.add(new Rook(this, 0, 0, false));
        piecesList.add(new Knight(this, 1, 0, false));
        piecesList.add(new Bishop(this, 2, 0, false));
        piecesList.add(new Queen(this, 3, 0, false));
        piecesList.add(new King(this, 4, 0, false));
        piecesList.add(new Bishop(this, 5, 0, false));
        piecesList.add(new Knight(this, 6, 0, false));
        piecesList.add(new Rook(this, 7, 0, false));

        piecesList.add(new Pawn(this, 0, 1, false));
        piecesList.add(new Pawn(this, 1, 1, false));
        piecesList.add(new Pawn(this, 2, 1, false));
        piecesList.add(new Pawn(this, 3, 1, false));
        piecesList.add(new Pawn(this, 4, 1, false));
        piecesList.add(new Pawn(this, 5, 1, false));
        piecesList.add(new Pawn(this, 6, 1, false));
        piecesList.add(new Pawn(this, 7, 1, false));

        piecesList.add(new Rook(this, 0, 7, true));
        piecesList.add(new Knight(this, 1, 7, true));
        piecesList.add(new Bishop(this, 2, 7, true));
        piecesList.add(new Queen(this, 3, 7, true));
        piecesList.add(new King(this, 4, 7, true));
        piecesList.add(new Bishop(this, 5, 7, true));
        piecesList.add(new Knight(this, 6, 7, true));
        piecesList.add(new Rook(this, 7, 7, true));

        piecesList.add(new Pawn(this, 0, 6, true));
        piecesList.add(new Pawn(this, 1, 6, true));
        piecesList.add(new Pawn(this, 2, 6, true));
        piecesList.add(new Pawn(this, 3, 6, true));
        piecesList.add(new Pawn(this, 4, 6, true));
        piecesList.add(new Pawn(this, 5, 6, true));
        piecesList.add(new Pawn(this, 6, 6, true));
        piecesList.add(new Pawn(this, 7, 6, true));
    }

    /**
     * Checks after every move whether the game is over by checkmate, stalemate
     * or insufficient material, and ends the game if so.
     */
    private void updateGameState() {
        Piece king = findKing(isWhitetoMove);
        if (checkScanner.isGameOver(king)) {
            if (checkScanner.isKingChecked(new Move(this, king, king.col, king.row))) {
                endGame(isWhitetoMove ? "Black Wins!!" : "White Wins!!");
            } else {
                endGame("Stalemate!");
            }
        } else if (insufficientMaterial(true) && insufficientMaterial(false)) {
            endGame("Insufficient Material!!");
        }
    }

    /**
     * Freezes the board in its final position, then shows the message on top of it.
     * The program is not closed, so the last position stays visible.
     *
     * @param line the message to show (for example "White Wins!!")
     */
    private void endGame(String line) {
        isGameOver = true;          // no more moves allowed
        selectedPiece = null;       // clear highlights
        paintImmediately(0, 0, getWidth(), getHeight());   // draw the final position NOW

        JOptionPane.showMessageDialog(this, line, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        // no System.exit: the window stays open showing the last state
    }

    /**
     * Checks whether one side has too few pieces to checkmate
     * (no queen, rook or pawn, and at most one other piece besides the king).
     *
     * @param isWhite true to check white, false to check black
     * @return true if that side cannot win by checkmate
     */
    private boolean insufficientMaterial(boolean isWhite) {
        ArrayList<String> names = piecesList.stream().filter(p -> p.isWhite == isWhite)
                .map(p -> p.name)
                .collect(Collectors.toCollection(ArrayList::new));

        if (names.contains("queen") || names.contains("pawn") || names.contains("rook")) return false;

        return names.size() < 3;
    }

    /**
     * Draws the squares, the highlights of the legal moves of the selected piece, and the pieces.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        //paint board
        for (int r =0; r < rows ; r++) {
            for (int c =0 ; c<cols; c++) {
                g2d.setColor((c + r) % 2 == 0 ? new Color(255, 224, 179) : new Color(56, 46, 138));
                g2d.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
            }
        }

        //paint highlights
        if (selectedPiece != null) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (isValidMove(new Move(this, selectedPiece, c, r))) {
                        g2d.setColor(new Color(75, 165, 70, 190));
                        g2d.fillRect(c*tileSize, r*tileSize, tileSize, tileSize);
                    }
                }
            }
        }

        //paint pieces
        for (Piece piece : piecesList) {
            piece.paint(g2d);
        }
    }
}
