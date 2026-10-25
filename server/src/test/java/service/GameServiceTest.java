package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    @Test
    public void listGamesSuccessEmpty() throws DataAccessException {
        // Create our DAOs
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        // Create our service
        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        AuthData auth = new AuthData("test-token", "bryce");
        authDAO.createAuth(auth);

        // Retrieve the list of games
        Collection<GameData> games = gameService.listGames("test-token");

        // Verify the result
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void listGamesSuccessWithGames() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        AuthData auth = new AuthData("test-token", "bryce");
        authDAO.createAuth(auth);

        // Create two chess games
        gameDAO.createGame(new GameData(
                0, null, null, "Game One", null
        ));

        gameDAO.createGame(new GameData(
                0, null, null, "Game Two", null
        ));

        // Retrieve all games
        Collection<GameData> games = gameService.listGames("test-token");

        // Verify both games were returned
        assertEquals(2, games.size());

        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("Game One")
        ));

        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("Game Two")
        ));
    }

    @Test
    public void listGamesInvalidToken() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Attempt to list games with an invalid token
        assertThrows(SecurityException.class, () -> {
            gameService.listGames("invalid-token");
        });
    }

    @Test
    public void listGamesMissingToken() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Attempt to list games without an authentication token
        assertThrows(SecurityException.class, () -> {
            gameService.listGames(null);
        });
    }
}