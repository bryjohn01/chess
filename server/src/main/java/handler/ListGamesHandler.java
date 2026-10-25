package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import service.GameService;

import java.util.Collection;

public class ListGamesHandler {
    private final GameService gameService;

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            Collection<GameData> games = gameService.listGames(authToken);

            var gameSummaries = games.stream()
                    .map(game -> new GameSummary(
                            game.gameID(),
                            game.whiteUsername(),
                            game.blackUsername(),
                            game.gameName()
                    ))
                    .toList();

            ctx.status(200);
            ctx.json(new ListGamesResponse(gameSummaries));

        } catch (SecurityException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record GameSummary(
            int gameID,
            String whiteUsername,
            String blackUsername,
            String gameName
    ) {}

    private record ListGamesResponse(
            Collection<GameSummary> games
    ) {}

    private record ErrorResponse(String message) {}
}