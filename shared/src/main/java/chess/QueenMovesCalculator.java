package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        addMovesInDirection(board, myPosition, moves, 1, 0);
        addMovesInDirection(board, myPosition, moves, -1, 0);
        addMovesInDirection(board, myPosition, moves, 0, 1);
        addMovesInDirection(board, myPosition, moves, 0, -1);

        addMovesInDirection(board, myPosition, moves, 1, 1);
        addMovesInDirection(board, myPosition, moves, 1, -1);
        addMovesInDirection(board, myPosition, moves, -1, 1);
        addMovesInDirection(board, myPosition, moves, -1, -1);

        return moves;
    }
}
