package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves (ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        addMove(board, myPosition, moves, 1, 0);
        addMove(board, myPosition, moves, -1, 0);
        addMove(board, myPosition, moves, 0, 1);
        addMove(board, myPosition, moves, 0, -1);

        addMove(board, myPosition, moves, 1, 1);
        addMove(board, myPosition, moves, 1, -1);
        addMove(board, myPosition, moves, -1, 1);
        addMove(board, myPosition, moves, -1, -1);

        return moves;
    }

    private void addMove(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int rowDirection,
            int colDirection) {
        ChessPiece myPiece = board.getPiece(myPosition);

        int row = myPosition.getRow() + rowDirection;
        int col = myPosition.getColumn() + colDirection;

        if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece pieceAtPosition = board.getPiece(newPosition);

            if (pieceAtPosition == null ||
                    pieceAtPosition.getTeamColor() != myPiece.getTeamColor()) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

    }
}
