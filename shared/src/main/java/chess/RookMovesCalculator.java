package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        addMovesInDirection(board, myPosition, moves, 1, 0);
        addMovesInDirection(board, myPosition, moves, -1, 0);
        addMovesInDirection(board, myPosition, moves, 0, 1);
        addMovesInDirection(board, myPosition, moves, 0, -1);

        return moves;
    }

    private void addMovesInDirection(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int rowDirection,
            int colDirection) {
        ChessPiece myPiece = board.getPiece(myPosition);

        int row = myPosition.getRow() + rowDirection;
        int col = myPosition.getColumn() + colDirection;

        while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece pieceAtPosition = board.getPiece(newPosition);

            if (pieceAtPosition == null) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
            else {
                if (pieceAtPosition.getTeamColor() != myPiece.getTeamColor()) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }

                break;
            }

            row += rowDirection;
            col += colDirection;
        }

    }
}
