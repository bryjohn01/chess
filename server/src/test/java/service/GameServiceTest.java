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

    @Test
    public void joinGameSuccessWhite() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        AuthData auth = new AuthData("test-token", "bryce");
        authDAO.createAuth(auth);

        // Create a chess game
        int gameID = gameService.createGame("test-token", "My Chess Game");

        // Join the game as WHITE
        gameService.joinGame("test-token", "WHITE", gameID);

        // Retrieve the updated game
        GameData game = gameDAO.getGame(gameID);

        // Verify the player joined correctly
        assertNotNull(game);
        assertEquals("bryce", game.whiteUsername());
        assertNull(game.blackUsername());
        assertEquals("My Chess Game", game.gameName());
        assertNotNull(game.game());
    }

    @Test
    public void joinGameSuccessBlack() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        AuthData auth = new AuthData("test-token", "bryce");
        authDAO.createAuth(auth);

        // Create a chess game
        int gameID = gameService.createGame("test-token", "My Chess Game");

        // Join the game as BLACK
        gameService.joinGame("test-token", "BLACK", gameID);

        // Retrieve the updated game
        GameData game = gameDAO.getGame(gameID);

        // Verify the player joined correctly
        assertNotNull(game);
        assertNull(game.whiteUsername());
        assertEquals("bryce", game.blackUsername());
        assertEquals("My Chess Game", game.gameName());
        assertNotNull(game.game());
    }

    @Test
    public void joinGamePositionAlreadyTaken() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create authentication tokens for two users
        authDAO.createAuth(new AuthData("token-one", "bryce"));
        authDAO.createAuth(new AuthData("token-two", "opponent"));

        // Create a chess game
        int gameID = gameService.createGame("token-one", "My Chess Game");

        // First user joins as WHITE
        gameService.joinGame("token-one", "WHITE", gameID);

        // Second user attempts to take the WHITE position
        assertThrows(IllegalStateException.class, () -> {
            gameService.joinGame("token-two", "WHITE", gameID);
        });

        // Verify the original player still owns the position
        GameData game = gameDAO.getGame(gameID);

        assertEquals("bryce", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void joinGameInvalidColor() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        authDAO.createAuth(new AuthData("test-token", "bryce"));

        // Create a chess game
        int gameID = gameService.createGame("test-token", "My Chess Game");

        // Attempt to join with invalid player colors
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.joinGame("test-token", "GREEN", gameID);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            gameService.joinGame("test-token", null, gameID);
        });

        // Verify neither position was assigned
        GameData game = gameDAO.getGame(gameID);

        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void joinGameInvalidGameID() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        authDAO.createAuth(new AuthData("test-token", "bryce"));

        // Attempt to join a game that doesn't exist
        assertThrows(IllegalArgumentException.class, () -> {
            gameService.joinGame("test-token", "WHITE", 999);
        });

        // Verify no games were created
        assertTrue(gameDAO.listGames().isEmpty());
    }

    @Test
    public void joinGameInvalidToken() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        authDAO.createAuth(new AuthData("valid-token", "bryce"));

        // Create a chess game
        int gameID = gameService.createGame("valid-token", "My Chess Game");

        // Attempt to join with an invalid authentication token
        assertThrows(SecurityException.class, () -> {
            gameService.joinGame("invalid-token", "WHITE", gameID);
        });

        // Verify neither player position was assigned
        GameData game = gameDAO.getGame(gameID);

        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    public void joinGameMissingToken() throws DataAccessException {
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        GameService gameService = new GameService(gameDAO, authDAO);

        // Create a valid authentication token
        authDAO.createAuth(new AuthData("valid-token", "bryce"));

        // Create a chess game
        int gameID = gameService.createGame("valid-token", "My Chess Game");

        // Attempt to join without an authentication token
        assertThrows(SecurityException.class, () -> {
            gameService.joinGame(null, "WHITE", gameID);
        });

        // Verify neither player position was assigned
        GameData game = gameDAO.getGame(gameID);

        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
    }
}