package Main;

import pieces.Piece;

/**
 * Detects check and the end of the game.
 * It looks outward from the king along every line a piece could attack from.
 */
public class CheckScanner {
    /** The board that is scanned. */
    Board board;

    /**
     * @param board the board to scan
     */
    public CheckScanner(Board board) {
        this.board = board;
    }

    /**
     * Checks whether the king would be in check after the given move is played.
     * If the king itself is the piece moving, its target square is used as its position.
     *
     * @param move the move to test
     * @return true if the king of the moving piece's colour would be attacked
     */
    public boolean isKingChecked(Move move) {
        Piece king = board.findKing(move.piece.isWhite);
        assert king != null;

        int kingCol = king.col;
        int kingRow = king.row;

        if (board.selectedPiece != null && board.selectedPiece.name.equals("king")) {
            kingCol = move.newCol;
            kingRow = move.newRow;
        }

        return hitByRook   (move.newCol, move.newRow, king, kingCol, kingRow, 0, 1)  || //UP
                hitByRook  (move.newCol, move.newRow, king, kingCol, kingRow, 1, 0)  || //right
                hitByRook  (move.newCol, move.newRow, king, kingCol, kingRow, 0, -1) || //down
                hitByRook  (move.newCol, move.newRow, king, kingCol, kingRow, -1, 0) || //left

                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, -1)|| //up-left
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, -1) || //up-right
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, 1, 1)  || //down-right
                hitByBishop(move.newCol, move.newRow, king, kingCol, kingRow, -1, 1) || //down-left

                hitByKnight(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByPawn(move.newCol, move.newRow, king, kingCol, kingRow) ||
                hitByKing(king, kingCol, kingRow);
    }

    /**
     * Looks from the king along one straight line and reports an enemy rook or queen
     * that attacks the king. The scan stops at the first piece it meets, and at the
     * move's target square because the moving piece will block the line there.
     *
     * @param col       target column of the move being tested
     * @param row       target row of the move being tested
     * @param king      the king that is being protected
     * @param kingCol   column of the king (after the move)
     * @param kingRow   row of the king (after the move)
     * @param colValue  column step of the line (-1, 0 or 1)
     * @param rowValue  row step of the line (-1, 0 or 1)
     * @return true if an enemy rook or queen attacks along this line
     */
    private boolean hitByRook(int col,int row, Piece king, int kingCol, int kingRow, int colValue, int rowValue) {
        for (int i = 1; i < 8; i++) {
            if (kingCol + (i*colValue) == col &&  kingRow + (i*rowValue) == row) break;

            Piece piece = board.getPiece(kingCol+(i*colValue), kingRow+(i*rowValue));
            if (piece != null && piece != board.selectedPiece) {
                if (! board.sameTeam(piece, king) && (piece.name.equals("rook") || piece.name.equals("queen"))) return true;
                break;
            }
        }
        return false;
    }

    /**
     * Looks from the king along one diagonal and reports an enemy bishop or queen
     * that attacks the king. Works like {@link #hitByRook}, but the row step is
     * applied in the opposite direction (see the arithmetic in the loop).
     *
     * @param col       target column of the move being tested
     * @param row       target row of the move being tested
     * @param king      the king that is being protected
     * @param kingCol   column of the king (after the move)
     * @param kingRow   row of the king (after the move)
     * @param colValue  column step of the diagonal (-1 or 1)
     * @param rowValue  row step of the diagonal (-1 or 1)
     * @return true if an enemy bishop or queen attacks along this diagonal
     */
    private boolean hitByBishop(int col,int row, Piece king, int kingCol, int kingRow, int colValue, int rowValue) {
        for (int i = 1; i < 8; i++) {
            if (kingCol - (i*colValue) == col &&  kingRow - (i*rowValue) == row) break;

            Piece piece = board.getPiece(kingCol+(i*colValue), kingRow - (i*rowValue));
            if (piece != null && piece != board.selectedPiece) {
                if (! board.sameTeam(piece, king) && (piece.name.equals("bishop") || piece.name.equals("queen"))) return true;
                break;
            }
        }
        return false;
    }

    /**
     * Checks the eight squares a knight could attack the king from.
     *
     * @param col     target column of the move being tested
     * @param row     target row of the move being tested
     * @param king    the king that is being protected
     * @param kingCol column of the king (after the move)
     * @param kingRow row of the king (after the move)
     * @return true if an enemy knight attacks the king
     */
    private boolean hitByKnight(int col,int row, Piece king, int kingCol, int kingRow) {
        return checkKnight(board.getPiece(kingCol - 1, kingRow - 2), king, col, row)||
                checkKnight(board.getPiece(kingCol + 1, kingRow - 2), king, col, row)||
                checkKnight(board.getPiece(kingCol + 2, kingRow - 1), king, col, row)||
                checkKnight(board.getPiece(kingCol + 2, kingRow + 1), king, col, row)||
                checkKnight(board.getPiece(kingCol + 1, kingRow + 2), king, col, row)||
                checkKnight(board.getPiece(kingCol - 1, kingRow + 2), king, col, row)||
                checkKnight(board.getPiece(kingCol - 2, kingRow + 1), king, col, row)||
                checkKnight(board.getPiece(kingCol - 2, kingRow - 1), king, col, row);

    }

    /**
     * @return true if {@code p} is an enemy knight that is not captured by the move
     *         (it is not standing on the target square)
     */
    private boolean checkKnight(Piece p, Piece k, int col, int row) {
        return p != null && !board.sameTeam(p,k) && p.name.equals("knight") && !(p.col == col && p.row == row);
    }

    /**
     * Checks the eight squares around the king for the enemy king,
     * because two kings may never stand next to each other.
     *
     * @param king    the king that is being protected
     * @param kingCol column of the king (after the move)
     * @param kingRow row of the king (after the move)
     * @return true if the enemy king is adjacent
     */
    private boolean hitByKing(Piece king, int kingCol, int kingRow) {
        return checkKing(board.getPiece(kingCol -1, kingRow - 1), king)||
                checkKing(board.getPiece(kingCol + 1, kingRow - 1), king)||
                checkKing(board.getPiece(kingCol, kingRow - 1), king)||
                checkKing(board.getPiece(kingCol -1, kingRow), king)||
                checkKing(board.getPiece(kingCol +1, kingRow), king)||
                checkKing(board.getPiece(kingCol -1, kingRow +1), king)||
                checkKing(board.getPiece(kingCol +1, kingRow +1), king)||
                checkKing(board.getPiece(kingCol, kingRow +1), king);
    }

    /**
     * @return true if {@code p} is the enemy king
     */
    private boolean checkKing(Piece p, Piece k) {
        return p != null && !board.sameTeam(p,k) && p.name.equals("king");
    }

    /**
     * Checks the two squares from which an enemy pawn could capture the king.
     *
     * @param col     target column of the move being tested
     * @param row     target row of the move being tested
     * @param king    the king that is being protected
     * @param kingCol column of the king (after the move)
     * @param kingRow row of the king (after the move)
     * @return true if an enemy pawn attacks the king
     */
    private boolean hitByPawn(int col, int row, Piece king, int kingCol, int kingRow) {
        int colorVal = king.isWhite ? -1 : 1;
        return checkPawn(board.getPiece(kingCol + 1, kingRow + colorVal), king, col, row)||
                checkPawn(board.getPiece(kingCol - 1, kingRow + colorVal), king, col, row);
    }

    /**
     * @return true if {@code p} is an enemy pawn that is not captured by the move
     *         (it is not standing on the target square)
     */
    private boolean checkPawn(Piece p, Piece k, int col, int row) {
        return p!= null && !board.sameTeam(p,k) && p.name.equals("pawn") && !(p.col == col && p.row == row);
    }

    /**
     * Checks whether the side of the given king has no legal move left.
     * Note: this changes {@code board.selectedPiece} while it tests the moves.
     *
     * @param king the king of the side that is to move
     * @return true if there is no legal move (checkmate or stalemate)
     */
    public boolean isGameOver(Piece king) {
        for (Piece piece : board.piecesList) {
            if (board.sameTeam(piece, king)) {
                board.selectedPiece = piece == king ? king : null;
                for (int row = 0; row< board.rows; row++) {
                    for (int col = 0; col< board.cols; col++) {
                        Move move = new Move(board, piece, col, row);
                        if (board.isValidMove(move)) return false;
                    }
                }
            }
        }

        return true;
    }
}
