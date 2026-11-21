package pieces;

import Main.Board;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Base class of all chess pieces. It stores the position, colour and picture of a piece.
 * Each subclass defines how that kind of piece moves.
 */
public class Piece {
    /** Column of the square the piece stands on. */
    public int col;
    /** Row of the square the piece stands on. */
    public int row;

    /** Horizontal drawing position in pixels (changes while the piece is dragged). */
    public int xPos;
    /** Vertical drawing position in pixels (changes while the piece is dragged). */
    public int yPos;

    /** True for a white piece, false for a black piece. */
    public boolean isWhite;
    /** Kind of the piece: "king", "queen", "rook", "bishop", "knight" or "pawn". */
    public String name;
    /** Value of the piece (not used yet). */
    public int value;

    /** True until the piece has moved; needed for castling and the two-square pawn push. */
    public boolean isFirstMove = true;

    /** The picture that contains all piece images, loaded once for all pieces. */
    static final BufferedImage sheet;
    static {
        try (InputStream in = Piece.class.getClassLoader().getResourceAsStream("Pieces.png")) {
            if (in == null) {
                throw new IllegalStateException("Pieces.png not found on the classpath");
            }
            sheet = ImageIO.read(in);
            if (sheet == null) {
                throw new IllegalStateException("Pieces.png could not be decoded as an image");
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // The sheet is 6 columns x 2 rows (white on top, black below). Use separate
    // cell width/height so a sheet that isn't exactly 3:1 (e.g. 1800x599) doesn't drift.
    /** Width and height of one piece image inside the sheet. */
    static final int cellW = sheet.getWidth() / 6;
    static final int cellH = sheet.getHeight() / 2;

    /** The image of this piece, scaled to the size of one square. */
    Image sprite;
    /** The board the piece belongs to. */
    Board board;

    /** @param spriteCol column in the sheet: king 0, queen 1, bishop 2, knight 3, rook 4, pawn 5 */
    public Piece(Board board, int col, int row, boolean isWhite, String name, int spriteCol) {
        this.board = board;
        this.col = col;
        this.row = row;
        this.xPos = col * board.tileSize;
        this.yPos = row * board.tileSize;
        this.isWhite = isWhite;
        this.name = name;
        this.sprite = sheet
                .getSubimage(spriteCol * cellW, isWhite ? 0 : cellH, cellW, cellH)
                .getScaledInstance(board.tileSize, board.tileSize, BufferedImage.SCALE_SMOOTH);
    }

    /**
     * Draws the piece at its current pixel position.
     *
     * @param g2d the graphics context to draw on
     */
    public void paint(Graphics2D g2d) {
        g2d.drawImage(sprite, xPos, yPos, null);
    }

    /**
     * Checks whether the piece can move to a square according to its own movement pattern.
     * It does not check for blocking pieces or for check; {@link Board#isValidMove} does that.
     *
     * @param col target column
     * @param row target row
     * @return true if the piece moves that way
     */
    public boolean isValidMovement(int col, int row) {return true;}
    /**
     * Checks whether another piece stands between the piece and the target square.
     *
     * @param col target column
     * @param row target row
     * @return true if the path is blocked
     */
    public boolean moveCollidesWithPiece(int col, int row) {return false;}

}