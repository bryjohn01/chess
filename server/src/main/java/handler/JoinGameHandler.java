package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.GameService;

public class JoinGameHandler {
    private final GameService gameService;

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void joinGame(Context ctx) {
        try {
            // Get the authentication token
            String authToken = ctx.header("Authorization");

            // Read the request body
            JoinGameRequest request = ctx.bodyAsClass(JoinGameRequest.class);

            // Validate the request
            if (request == null || request.gameID() == null) {
                throw new IllegalArgumentException("Error: bad request");
            }

            // Join the game
            gameService.joinGame(
                    authToken,
                    request.playerColor(),
                    request.gameID()
            );

            // Return success
            ctx.status(200);
            ctx.json(new EmptyResponse());

        } catch (SecurityException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));

        } catch (IllegalStateException e) {
            ctx.status(403);
            ctx.json(new ErrorResponse("Error: already taken"));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record JoinGameRequest(String playerColor, Integer gameID) {}

    private record EmptyResponse() {}

    private record ErrorResponse(String message) {}
}