package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;

public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context ctx) {
        try {
            // Get the authentication token
            String authToken = ctx.header("Authorization");

            // Read the game name from the request body
            CreateGameRequest request = ctx.bodyAsClass(CreateGameRequest.class);

            // Create the game
            int gameID = gameService.createGame(authToken, request.gameName());

            // Return the assigned game ID
            ctx.status(200);
            ctx.json(new CreateGameResponse(gameID));

        } catch (SecurityException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record CreateGameRequest(String gameName) {}

    private record CreateGameResponse(int gameID) {}

    private record ErrorResponse(String message) {}
}