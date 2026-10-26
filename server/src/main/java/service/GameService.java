package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import java.util.Collection;
import chess.ChessGame;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public Collection<GameData> listGames(String authToken)
            throws DataAccessException {

        // Verify that the user is authenticated
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new SecurityException("Error: unauthorized");
        }

        // Retrieve all games
        return gameDAO.listGames();
    }

    public int createGame(String authToken, String gameName)
            throws DataAccessException {

        // Verify that the user is authenticated
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new SecurityException("Error: unauthorized");
        }

        // Verify that the game name is valid
        if (gameName == null || gameName.isBlank()) {
            throw new IllegalArgumentException("Error: bad request");
        }

        // Create a new chess game
        GameData game = new GameData(
                0,
                null,
                null,
                gameName,
                new ChessGame()
        );

        // Store the game and return its assigned ID
        return gameDAO.createGame(game);
    }

    public void joinGame(String authToken, String playerColor, int gameID)
            throws DataAccessException {

        // Verify authentication
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new SecurityException("Error: unauthorized");
        }

        // Verify the player color
        if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            throw new IllegalArgumentException("Error: bad request");
        }

        // Find the requested game
        GameData game = gameDAO.getGame(gameID);

        if (game == null) {
            throw new IllegalArgumentException("Error: bad request");
        }

        // Get the username associated with the token
        String username = authDAO.getAuth(authToken).username();

        // Check whether the selected position is available
        if ("WHITE".equals(playerColor) && game.whiteUsername() != null) {
            throw new IllegalStateException("Error: already taken");
        }

        if ("BLACK".equals(playerColor) && game.blackUsername() != null) {
            throw new IllegalStateException("Error: already taken");
        }

        // Update the selected player position
        GameData updatedGame;

        if ("WHITE".equals(playerColor)) {
            updatedGame = new GameData(
                    game.gameID(),
                    username,
                    game.blackUsername(),
                    game.gameName(),
                    game.game()
            );
        } else {
            updatedGame = new GameData(
                    game.gameID(),
                    game.whiteUsername(),
                    username,
                    game.gameName(),
                    game.game()
            );
        }

        // Save the updated game
        gameDAO.updateGame(updatedGame);
    }
}