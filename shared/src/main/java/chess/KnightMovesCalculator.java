package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        SingleMoveCalculator.addMove(board, myPosition, moves, 2, 1);
        SingleMoveCalculator.addMove(board, myPosition, moves, 2, -1);
        SingleMoveCalculator.addMove(board, myPosition, moves, -2, 1);
        SingleMoveCalculator.addMove(board, myPosition, moves, -2, -1);

        SingleMoveCalculator.addMove(board, myPosition, moves, 1, 2);
        SingleMoveCalculator.addMove(board, myPosition, moves, 1, -2);
        SingleMoveCalculator.addMove(board, myPosition, moves, -1, 2);
        SingleMoveCalculator.addMove(board, myPosition, moves, -1, -2);

        return moves;
    }
}