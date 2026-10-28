package dataaccess;

import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() throws DataAccessException {
        DatabaseManager.createDatabase();

        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(256) NOT NULL PRIMARY KEY,
                    password VARCHAR(256) NOT NULL,
                    email VARCHAR(256) NOT NULL
                )
                """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create users table", e);
        }

        String authSql = """
        CREATE TABLE IF NOT EXISTS auth (
            authToken VARCHAR(256) NOT NULL PRIMARY KEY,
            username VARCHAR(256) NOT NULL
        )
        """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(authSql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create auth table", e);
        }

        String gamesSql = """
        CREATE TABLE IF NOT EXISTS games (
            gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
            whiteUsername VARCHAR(256),
            blackUsername VARCHAR(256),
            gameName VARCHAR(256) NOT NULL,
            game TEXT NOT NULL
        )
        """;

        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement(gamesSql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create games table", e);
        }
    }
}