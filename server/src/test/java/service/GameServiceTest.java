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

    @Test
    public void createGameSuccess() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        AuthData auth = new AuthData("test-token", "bryce");
        authDAO.createAuth(auth);

        // Create a new chess game
        int gameID = gameService.createGame("test-token", "My Chess Game");

        // Verify a valid game ID was assigned
        assertTrue(gameID > 0);

        // Retrieve the newly created game
        GameData game = gameDAO.getGame(gameID);

        // Verify the game was stored correctly
        assertNotNull(game);
        assertEquals("My Chess Game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertNotNull(game.game());
    }

    @Test
    public void createGameInvalidToken() {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Attempt to create a game with an invalid token
        assertThrows(SecurityException.class, () -> {
            gameService.createGame("invalid-token", "My Chess Game");
        });
    }

    @Test
    public void createGameMissingName() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        AuthData auth = new AuthData("test-token", "bryce");
        authDAO.createAuth(auth);

        // Attempt to create games with invalid names
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.createGame("test-token", null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.createGame("test-token", "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.createGame("test-token", "   ");
        });

        // Verify that no games were created
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void createGameMissingToken() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Attempt to create a game without authentication
        assertThrows(SecurityException.class, () -> {
            gameService.createGame(null, "My Chess Game");
        });

        // Verify that no game was created
        assertTrue(gameDAO.listGames().isEmpty());
    }
}