package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;
import java.util.Collection;
import java.util.List;
import dataaccess.DatabaseManager;
import java.sql.SQLException;
import com.google.gson.Gson;
import java.sql.Statement;
import chess.ChessGame;
import java.util.ArrayList;

public class SQLGameDAO implements GameDAO {
    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear games", e);
        }
    }

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String sql = """
            INSERT INTO games (whiteUsername, blackUsername, gameName, game)
            VALUES (?, ?, ?, ?)
            """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, new Gson().toJson(game.game()));

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new DataAccessException("Failed to retrieve generated game ID");

        } catch (SQLException e) {
            throw new DataAccessException("Failed to create game", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setInt(1, gameID);

            try (var result = statement.executeQuery()) {
                if (result.next()) {
                    ChessGame chessGame = new Gson().fromJson(
                            result.getString("game"),
                            ChessGame.class
                    );

                    return new GameData(
                            result.getInt("gameID"),
                            result.getString("whiteUsername"),
                            result.getString("blackUsername"),
                            result.getString("gameName"),
                            chessGame
                    );
                }
            }

            return null;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game", e);
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        String sql = "SELECT * FROM games";
        Collection<GameData> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql);
             var result = statement.executeQuery()) {

            while (result.next()) {
                ChessGame chessGame = new Gson().fromJson(
                        result.getString("game"),
                        ChessGame.class
                );

                GameData game = new GameData(
                        result.getInt("gameID"),
                        result.getString("whiteUsername"),
                        result.getString("blackUsername"),
                        result.getString("gameName"),
                        chessGame
                );

                games.add(game);
            }

            return games;
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games", e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = """
            UPDATE games
            SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
            WHERE gameID = ?
            """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {

            statement.setString(1, game.whiteUsername());
            statement.setString(2, game.blackUsername());
            statement.setString(3, game.gameName());
            statement.setString(4, new Gson().toJson(game.game()));
            statement.setInt(5, game.gameID());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game", e);
        }
    }
}