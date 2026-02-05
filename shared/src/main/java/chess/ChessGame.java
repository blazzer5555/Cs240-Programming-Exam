package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor player_turn;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        player_turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return player_turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        player_turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        }
        ChessPiece pieceToMove = board.getPiece(startPosition);
        Collection<ChessMove> movesOfPiece = pieceToMove.pieceMoves(board, startPosition);
        List<ChessMove> validMovesOfPiece = new ArrayList<>();
        for (ChessMove move : movesOfPiece) {
            ChessBoard newBoard = board.deepCopy();
            ChessBoard oldBoard = board.deepCopy();
            newBoard.addPiece(move.getEndPosition(), pieceToMove);
            newBoard.addPiece(move.getStartPosition(), null);
            board = newBoard.deepCopy();
            if (!isInCheck(pieceToMove.getTeamColor())) {
                validMovesOfPiece.add(move);
            }
            board = oldBoard.deepCopy();
        }
        return validMovesOfPiece;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // PROMOTE PAWNS
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        if (pieceToMove == null) {
            throw new InvalidMoveException("There is no piece at that location");
        }
        if (pieceToMove.getTeamColor() != player_turn) {
            throw  new InvalidMoveException("It is not this piece's turn");
        }
        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        for (ChessMove potentialMove: possibleMoves) {
            if (potentialMove.equals(move)) {
                if (move.getPromotionPiece() != null) {
                    ChessPiece promotedPiece = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
                    board.addPiece(move.getEndPosition(), promotedPiece);
                }
                else {
                    board.addPiece(move.getEndPosition(), pieceToMove);
                }
                board.addPiece(move.getStartPosition(), null);
                if (player_turn == TeamColor.WHITE) {
                    player_turn = TeamColor.BLACK;
                }
                else {
                    player_turn = TeamColor.WHITE;
                }
                return;
            }
        }
        throw new InvalidMoveException("That piece cannot make that move");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> allPossibleEnemyMoves;
        if (teamColor == TeamColor.WHITE) {
            allPossibleEnemyMoves = getAllPossibleMoves(TeamColor.BLACK);
        }
        else {
            allPossibleEnemyMoves = getAllPossibleMoves(TeamColor.WHITE);
        }
        ChessPosition kingPosition = getKingPosition(teamColor);
        for (ChessMove move: allPossibleEnemyMoves) {
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    private Collection<ChessMove> getAllPossibleMoves(TeamColor team) {
        Collection<ChessMove> allPossibleMoves = new ArrayList<>();
        for (int row = 0; row< 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                if (board.getPiece(position) != null) {
                    if (board.getPiece(position).getTeamColor() == team) {
                        allPossibleMoves.addAll(board.getPiece(position).pieceMoves(board, position));
                    }
                }
            }
        }
        return allPossibleMoves;
    }


    private ChessPosition getKingPosition(TeamColor teamColor) {
        for (int row = 0; row< 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                if (board.getPiece(position) != null) {
                    if (board.getPiece(position).getTeamColor() == teamColor & board.getPiece(position).getPieceType() == ChessPiece.PieceType.KING) {
                        return position;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return doesNotHaveAnyValidMove(teamColor);
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return doesNotHaveAnyValidMove(teamColor);
        }
        return false;
    }

    private boolean doesNotHaveAnyValidMove(TeamColor teamColor) {
        Collection<ChessMove> allPossibleMoves = getAllPossibleMoves(teamColor);
        for (ChessMove move : allPossibleMoves) {
            ChessBoard newBoard = board.deepCopy();
            ChessBoard oldBoard = board.deepCopy();
            ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
            newBoard.addPiece(move.getEndPosition(), pieceToMove);
            newBoard.addPiece(move.getStartPosition(), null);
            board = newBoard.deepCopy();
            if (!isInCheck(pieceToMove.getTeamColor())) {
                board = oldBoard.deepCopy();
                return false;
            }
            board = oldBoard.deepCopy();
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && player_turn == chessGame.player_turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, player_turn);
    }
}
