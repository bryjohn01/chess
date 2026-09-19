package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece myPiece = board.getPiece(myPosition);

        int direction;
        int startRow;
        int promotionRow;

        if (myPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startRow = 2;
            promotionRow = 8;
        } else {
            direction = -1;
            startRow = 7;
            promotionRow = 1;
        }

        int row = myPosition.getRow() + direction;
        int col = myPosition.getColumn();

        if (row >= 1 && row <= 8) {
            ChessPosition newPosition = new ChessPosition(row, col);

            if (board.getPiece(newPosition) == null) {
                if (row == promotionRow) {
                    addPromotionMoves(moves, myPosition, newPosition);
                } else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }

                if (myPosition.getRow() == startRow) {
                    int twoRow = myPosition.getRow() + (2 * direction);
                    ChessPosition twoPosition = new ChessPosition(twoRow, col);

                    if (board.getPiece(twoPosition) == null) {
                        moves.add(new ChessMove(myPosition, twoPosition, null));
                    }
                }
            }

            for (int colOffset : new int[]{-1, 1}) {
                int captureCol = col + colOffset;

                if (captureCol >= 1 && captureCol <= 8) {
                    ChessPosition capturePosition = new ChessPosition(row, captureCol);
                    ChessPiece capturePiece = board.getPiece(capturePosition);

                    if (capturePiece != null &&
                            capturePiece.getTeamColor() != myPiece.getTeamColor()) {

                        if (row == promotionRow) {
                            addPromotionMoves(moves, myPosition, capturePosition);
                        } else {
                            moves.add(new ChessMove(myPosition, capturePosition, null));
                        }
                    }
                }
            }
        }

        return moves;
    }

    private void addPromotionMoves(
            Collection<ChessMove> moves,
            ChessPosition myPosition,
            ChessPosition newPosition) {

        moves.add(new ChessMove(
                myPosition, newPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(
                myPosition, newPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(
                myPosition, newPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(
                myPosition, newPosition, ChessPiece.PieceType.KNIGHT));
    }
}
