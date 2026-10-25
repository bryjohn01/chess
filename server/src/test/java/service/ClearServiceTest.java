package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;
import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    @Test
    public void clearRemovesUsers() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        userDAO.createUser(
                new UserData("bryce", "password123", "bryce@example.com")
        );

        ClearService clearService =
                new ClearService(userDAO, authDAO, gameDAO);

        clearService.clear();

        assertNull(userDAO.getUser("bryce"));
    }

    @Test
    public void clearRemovesAuthTokens() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        AuthData auth = new AuthData("test-token", "bryce");
        authDAO.createAuth(auth);

        ClearService clearService =
                new ClearService(userDAO, authDAO, gameDAO);

        clearService.clear();

        assertNull(authDAO.getAuth("test-token"));
    }

    @Test
    public void clearRemovesGames() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameData game = new GameData(
                0,
                null,
                null,
                "Test Game",
                new ChessGame()
        );

        int gameID = gameDAO.createGame(game);

        ClearService clearService =
                new ClearService(userDAO, authDAO, gameDAO);

        clearService.clear();

        assertNull(gameDAO.getGame(gameID));
    }
}