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
}