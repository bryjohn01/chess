package dataaccess;

import dataaccess.sql.SQLGameDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import chess.ChessGame;
import model.GameData;

import static org.junit.jupiter.api.Assertions.*;

public class SQLGameDAOTests {

    private SQLGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseInitializer.initialize();
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    public void getNonexistentGame() throws DataAccessException {
        assertNull(gameDAO.getGame(999999));
    }

    @Test
    public void createAndGetGame() throws DataAccessException {
        ChessGame chessGame = new ChessGame();

        GameData game = new GameData(
                0,
                null,
                null,
                "Test Game",
                chessGame
        );

        int gameID = gameDAO.createGame(game);

        assertTrue(gameID > 0);

        GameData retrievedGame = gameDAO.getGame(gameID);

        assertNotNull(retrievedGame);
        assertEquals(gameID, retrievedGame.gameID());
        assertEquals("Test Game", retrievedGame.gameName());
        assertNotNull(retrievedGame.game());
        assertEquals(chessGame.getTeamTurn(), retrievedGame.game().getTeamTurn());
        assertEquals(chessGame.getBoard(), retrievedGame.game().getBoard());
    }

    @Test
    public void listGames() throws DataAccessException {
        GameData firstGame = new GameData(
                0, null, null, "First Game", new ChessGame()
        );

        GameData secondGame = new GameData(
                0, null, null, "Second Game", new ChessGame()
        );

        gameDAO.createGame(firstGame);
        gameDAO.createGame(secondGame);

        var games = gameDAO.listGames();

        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("First Game")
        ));
        assertTrue(games.stream().anyMatch(
                game -> game.gameName().equals("Second Game")
        ));
    }

    @Test
    public void updateGame() throws DataAccessException {
        GameData originalGame = new GameData(
                0, null, null, "Original Game", new ChessGame()
        );

        int gameID = gameDAO.createGame(originalGame);

        GameData updatedGame = new GameData(
                gameID, "player1", "player2",
                "Updated Game", new ChessGame()
        );

        gameDAO.updateGame(updatedGame);

        GameData retrievedGame = gameDAO.getGame(gameID);

        assertNotNull(retrievedGame);
        assertEquals(gameID, retrievedGame.gameID());
        assertEquals("player1", retrievedGame.whiteUsername());
        assertEquals("player2", retrievedGame.blackUsername());
        assertEquals("Updated Game", retrievedGame.gameName());
        assertEquals(updatedGame.game(), retrievedGame.game());
    }

    @Test
    public void clearGames() throws DataAccessException {
        GameData game = new GameData(
                0, null, null, "Test Game", new ChessGame()
        );

        int gameID = gameDAO.createGame(game);

        assertNotNull(gameDAO.getGame(gameID));

        gameDAO.clear();

        assertTrue(gameDAO.listGames().isEmpty());
        assertNull(gameDAO.getGame(gameID));
    }

    @Test
    public void updateNonexistentGame() throws DataAccessException {
        GameData nonexistentGame = new GameData(
                999999,
                null,
                null,
                "Nonexistent Game",
                new ChessGame()
        );

        gameDAO.updateGame(nonexistentGame);

        assertNull(gameDAO.getGame(999999));
        assertTrue(gameDAO.listGames().isEmpty());
    }
}