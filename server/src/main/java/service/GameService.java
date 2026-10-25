package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.GameData;
import java.util.Collection;

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
}