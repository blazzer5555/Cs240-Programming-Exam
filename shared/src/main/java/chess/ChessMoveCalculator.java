package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChessMoveCalculator {
    private ChessBoard board;
    private ChessPosition position;
    private ChessPiece piece;

    public ChessMoveCalculator(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
        this.piece = board.getPiece(position);
    }

    public Collection<ChessMove> calculateMove() {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return movePawn();
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return moveRook();
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return moveBishop();
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return moveQueen();
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            return moveKing();
        }
        else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return moveKnight();
        }
        else {
            throw new RuntimeException("That's a weird piece!");
        }
    }

    private boolean checkSquare(Collection<ChessMove> validMoves, ChessPosition newPosition) {
        if (board.getPiece(newPosition) == null) {
            validMoves.add(new ChessMove(position, newPosition, null));
            return false;
        }
        else {
            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                validMoves.add(new ChessMove(position, newPosition, null));
            }
            return true;
        }
    }

    private void checkPawnPromotion(Collection<ChessMove> validMoves, ChessPosition newPosition) {
        if (newPosition.getRow() == 8 || newPosition.getRow() == 1) {
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
            validMoves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
        }
        else {
            validMoves.add(new ChessMove(position, newPosition, null));
        }
    }

    private Collection<ChessMove> movePawn() {
        List<ChessMove> validMoves = new ArrayList<>();
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + 1, position.getColumn());
            if (board.getPiece(newPosition) == null) {
                checkPawnPromotion(validMoves, newPosition);
                if (position.getRow() == 2) {
                    ChessPosition initialPawnMovePosition = new ChessPosition(position.getRow() + 2, position.getColumn());
                    if (board.getPiece(initialPawnMovePosition) == null) {
                        validMoves.add(new ChessMove(position, initialPawnMovePosition, null));
                    }
                }
            }
            if (position.getColumn() + 1 <= 8 ) {
                ChessPosition upRightCapture = new ChessPosition(position.getRow() + 1, position.getColumn() + 1);
                if (board.getPiece(upRightCapture) != null) {
                    if (board.getPiece(upRightCapture).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        checkPawnPromotion(validMoves, upRightCapture);
                    }
                }
            }
            if (position.getColumn() - 1 >= 1 ) {
                ChessPosition upLeftCapture = new ChessPosition(position.getRow() + 1, position.getColumn() - 1);
                if (board.getPiece(upLeftCapture) != null) {
                    if (board.getPiece(upLeftCapture).getTeamColor() == ChessGame.TeamColor.BLACK) {
                        checkPawnPromotion(validMoves, upLeftCapture);
                    }
                }
            }
        }
        else {
            ChessPosition newPosition = new ChessPosition(position.getRow() - 1, position.getColumn());
            if (board.getPiece(newPosition) == null) {
                checkPawnPromotion(validMoves, newPosition);
                if (position.getRow() == 7) {
                    ChessPosition initialPawnMovePosition = new ChessPosition(position.getRow() - 2, position.getColumn());
                    if (board.getPiece(initialPawnMovePosition) == null) {
                        validMoves.add(new ChessMove(position, initialPawnMovePosition, null));
                    }
                }
            }
            if (position.getColumn() + 1 <= 8 ) {
                ChessPosition downRightCapture = new ChessPosition(position.getRow() - 1, position.getColumn() + 1);
                if (board.getPiece(downRightCapture) != null) {
                    if (board.getPiece(downRightCapture).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        checkPawnPromotion(validMoves, downRightCapture);
                    }
                }
            }
            if (position.getColumn() - 1 >= 1 ) {
                ChessPosition downLeftCapture = new ChessPosition(position.getRow() - 1, position.getColumn() - 1);
                if (board.getPiece(downLeftCapture) != null) {
                    if (board.getPiece(downLeftCapture).getTeamColor() == ChessGame.TeamColor.WHITE) {
                        checkPawnPromotion(validMoves, downLeftCapture);
                    }
                }
            }
        }
        return validMoves;
    }

    private Collection<ChessMove> moveRook() {
        List<ChessMove> validMoves = new ArrayList<>();
        int i = 1;
        boolean pieceBlocking = false;
        while (position.getRow() + i <= 8 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + i, position.getColumn());
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        i = 1;
        pieceBlocking = false;
        while (position.getRow() - i >= 1 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow() - i, position.getColumn());
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        i = 1;
        pieceBlocking = false;
        while (position.getColumn() + i <= 8 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow(), position.getColumn() + i);
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        i = 1;
        pieceBlocking = false;
        while (position.getColumn() - i >= 1 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow(), position.getColumn() - i);
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        return validMoves;
    }

    private Collection<ChessMove> moveBishop() {
        List<ChessMove> validMoves = new ArrayList<>();
        int i = 1;
        boolean pieceBlocking = false;
        while (position.getRow() + i <= 8 && position.getColumn() + i <= 8 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + i, position.getColumn() + i);
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        i = 1;
        pieceBlocking = false;
        while (position.getRow() - i >= 1 && position.getColumn() + i <= 8 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow() - i, position.getColumn() + i);
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        i = 1;
        pieceBlocking = false;
        while (position.getRow() - i >= 1 && position.getColumn() - i >= 1 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow() - i, position.getColumn() - i);
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        i = 1;
        pieceBlocking = false;
        while (position.getRow() + i <= 8 && position.getColumn() - i >= 1 && !pieceBlocking) {
            ChessPosition newPosition = new ChessPosition(position.getRow() + i, position.getColumn() - i);
            pieceBlocking = checkSquare(validMoves, newPosition);
            i++;
        }
        return validMoves;
    }

    private Collection<ChessMove> moveQueen() {
        Collection<ChessMove> validMoves = moveRook();
        validMoves.addAll(moveBishop());
        return validMoves;
    }

    private Collection<ChessMove> moveKing() {
        List<ChessMove> validMoves = new ArrayList<>();
        if (position.getRow() + 1 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow() + 1, position.getColumn()));
        }
        if (position.getRow() + 1 <= 8 && position.getColumn() + 1 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow() + 1, position.getColumn() + 1));
        }
        if (position.getColumn() + 1 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow(), position.getColumn() + 1));
        }
        if (position.getRow() - 1 >= 1 && position.getColumn() + 1 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow() - 1, position.getColumn() + 1));
        }
        if (position.getRow() - 1 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow() - 1, position.getColumn()));
        }
        if (position.getRow() - 1 >= 1 && position.getColumn() - 1 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow() - 1, position.getColumn() - 1));
        }
        if (position.getColumn() - 1 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow(), position.getColumn() - 1));
        }
        if (position.getRow() + 1 <= 8 && position.getColumn() - 1 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow() + 1, position.getColumn() - 1));
        }
        return validMoves;
    }

    private Collection<ChessMove> moveKnight() {
        List<ChessMove> validMoves = new ArrayList<>();
        if (position.getRow() + 1 <= 8 && position.getColumn() + 2 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow() + 1, position.getColumn() + 2));
        }
        if (position.getRow() - 1 >= 1 && position.getColumn() + 2 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow() - 1, position.getColumn() + 2));
        }
        if (position.getRow() + 1 <= 8 && position.getColumn() - 2 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow() + 1, position.getColumn() - 2));
        }
        if (position.getRow() - 1 >= 1 && position.getColumn() - 2 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow() - 1, position.getColumn() - 2));
        }
        if (position.getRow() + 2 <= 8 && position.getColumn() + 1 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow() + 2, position.getColumn() + 1));
        }
        if (position.getRow() - 2 >= 1 && position.getColumn() + 1 <= 8) {
            checkSquare(validMoves, new ChessPosition(position.getRow() - 2, position.getColumn() + 1));
        }
        if (position.getRow() + 2 <= 8 && position.getColumn() - 1 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow() + 2, position.getColumn() - 1));
        }
        if (position.getRow() - 2 >= 1 && position.getColumn() - 1 >= 1) {
            checkSquare(validMoves, new ChessPosition(position.getRow() - 2, position.getColumn() - 1));
        }
        return validMoves;
    }

}
