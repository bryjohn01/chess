package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessBoardRenderer {

    public void drawBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        boolean whitePerspective = perspective == ChessGame.TeamColor.WHITE;

        int startRow = whitePerspective ? 8 : 1;
        int endRow = whitePerspective ? 1 : 8;
        int rowStep = whitePerspective ? -1 : 1;

        int startCol = whitePerspective ? 1 : 8;
        int endCol = whitePerspective ? 8 : 1;
        int colStep = whitePerspective ? 1 : -1;

        for (int row = startRow; whitePerspective ? row >= endRow : row <= endRow; row += rowStep) {
            System.out.print(row + " ");

            for (int col = startCol; whitePerspective ? col <= endCol : col >= endCol; col += colStep) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                String background = (row + col) % 2 == 0
                        ? EscapeSequences.SET_BG_COLOR_DARK_GREY
                        : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;

                System.out.print(background);
                System.out.print(piece == null ? EscapeSequences.EMPTY : getPieceSymbol(piece));
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }

            System.out.println();
        }

        System.out.print("  ");

        for (int col = startCol;
             whitePerspective ? col <= endCol : col >= endCol;
             col += colStep) {

            System.out.print(" " + (char) ('a' + col - 1) + " ");
        }

        System.out.println();
    }

    private String getPieceSymbol(ChessPiece piece) {
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING -> white ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> white ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> white ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> white ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> white ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> white ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}