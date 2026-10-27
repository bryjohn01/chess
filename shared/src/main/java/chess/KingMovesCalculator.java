package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> pieceMoves (ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        SingleMoveCalculator.addMove(board, myPosition, moves, 1, 0);
        SingleMoveCalculator.addMove(board, myPosition, moves, -1, 0);
        SingleMoveCalculator.addMove(board, myPosition, moves, 0, 1);
        SingleMoveCalculator.addMove(board, myPosition, moves, 0, -1);

        SingleMoveCalculator.addMove(board, myPosition, moves, 1, 1);
        SingleMoveCalculator.addMove(board, myPosition, moves, 1, -1);
        SingleMoveCalculator.addMove(board, myPosition, moves, -1, 1);
        SingleMoveCalculator.addMove(board, myPosition, moves, -1, -1);

        return moves;
    }
}
