package chess;

import java.util.Collection;

public class SingleMoveCalculator {

    public static void addMove(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int rowOffset,
            int colOffset) {

        ChessPiece myPiece = board.getPiece(myPosition);

        int row = myPosition.getRow() + rowOffset;
        int col = myPosition.getColumn() + colOffset;

        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return;
        }

        ChessPosition newPosition = new ChessPosition(row, col);
        ChessPiece pieceAtPosition = board.getPiece(newPosition);

        if (pieceAtPosition == null ||
                pieceAtPosition.getTeamColor() != myPiece.getTeamColor()) {
            moves.add(new ChessMove(myPosition, newPosition, null));
        }
    }
}