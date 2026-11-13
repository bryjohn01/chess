package client;

import org.junit.jupiter.api.*;
import server.Server;
import model.AuthData;
import dataaccess.DatabaseManager;


public class ServerFacadeTests {

    private static Server server;

    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.createStatement()) {

            statement.executeUpdate("DELETE FROM auth");
            statement.executeUpdate("DELETE FROM games");
            statement.executeUpdate("DELETE FROM users");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerSuccess() throws Exception {
        AuthData auth = facade.register(
                "testplayer",
                "password123",
                "test@example.com"
        );

        Assertions.assertNotNull(auth);
        Assertions.assertEquals("testplayer", auth.username());
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertFalse(auth.authToken().isEmpty());
    }


    @Test
    public void registerDuplicateUsername() throws Exception {
        facade.register(
                "duplicateplayer",
                "password123",
                "first@example.com"
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> facade.register(
                        "duplicateplayer",
                        "differentpassword",
                        "second@example.com"
                )
        );

        Assertions.assertEquals(
                "Registration failed: Username is already taken",
                exception.getMessage()
        );
    }

    @Test
    public void loginSuccess() throws Exception {
        // Register a user first so they exist in the database.
        facade.register(
                "loginplayer",
                "password123",
                "login@example.com"
        );

        // Log in with the registered credentials.
        AuthData auth = facade.login(
                "loginplayer",
                "password123"
        );

        Assertions.assertNotNull(auth);
        Assertions.assertEquals("loginplayer", auth.username());
        Assertions.assertNotNull(auth.authToken());
        Assertions.assertFalse(auth.authToken().isEmpty());
    }

    @Test
    public void loginIncorrectPassword() throws Exception {
        // Register a user with a known password.
        facade.register(
                "loginplayer",
                "password123",
                "login@example.com"
        );

        // Attempt to log in with an incorrect password.
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> facade.login(
                        "loginplayer",
                        "wrongpassword"
                )
        );

        Assertions.assertEquals(
                "Login failed: Incorrect username or password",
                exception.getMessage()
        );
    }

    @Test
    public void logoutSuccess() throws Exception {
        // Register a user and obtain an authentication token.
        AuthData auth = facade.register(
                "logoutplayer",
                "password123",
                "logout@example.com"
        );

        // Log out using the authentication token.
        Assertions.assertDoesNotThrow(
                () -> facade.logout(auth.authToken())
        );

        // Verify the token can no longer be used.
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> facade.logout(auth.authToken())
        );

        Assertions.assertEquals(
                "Logout failed: Invalid or expired authentication token",
                exception.getMessage()
        );
    }

    @Test
    public void logoutInvalidToken() {
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> facade.logout("invalid-auth-token")
        );

        Assertions.assertEquals(
                "Logout failed: Invalid or expired authentication token",
                exception.getMessage()
        );
    }

    @Test
    public void createGameSuccess() throws Exception {
        // Register a user and obtain an authentication token.
        AuthData auth = facade.register(
                "gamecreator",
                "password123",
                "creator@example.com"
        );

        // Create a chess game.
        int gameID = facade.createGame(
                auth.authToken(),
                "My First Chess Game"
        );

        // Verify the server returned a valid game ID.
        Assertions.assertTrue(gameID > 0);
    }

    @Test
    public void createGameInvalidToken() {
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> facade.createGame(
                        "invalid-auth-token",
                        "Unauthorized Game"
                )
        );

        Assertions.assertEquals(
                "Create game failed: Invalid or expired authentication token",
                exception.getMessage()
        );
    }

    @Test
    public void listGamesSuccess() throws Exception {
        // Register a user and obtain an authentication token.
        AuthData auth = facade.register(
                "gameviewer",
                "password123",
                "viewer@example.com"
        );

        // Create two games.
        int firstGameID = facade.createGame(
                auth.authToken(),
                "First Chess Game"
        );

        int secondGameID = facade.createGame(
                auth.authToken(),
                "Second Chess Game"
        );

        // Retrieve the list of games.
        var games = facade.listGames(auth.authToken());

        // Verify both games were returned.
        Assertions.assertEquals(2, games.size());

        Assertions.assertTrue(games.stream().anyMatch(game ->
                game.gameID() == firstGameID
                        && game.gameName().equals("First Chess Game")
        ));

        Assertions.assertTrue(games.stream().anyMatch(game ->
                game.gameID() == secondGameID
                        && game.gameName().equals("Second Chess Game")
        ));

        // Neither game should have players assigned yet.
        games.forEach(game -> {
            Assertions.assertNull(game.whiteUsername());
            Assertions.assertNull(game.blackUsername());
        });
    }

    @Test
    public void listGamesInvalidToken() {
        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> facade.listGames("invalid-auth-token")
        );

        Assertions.assertEquals(
                "List games failed: Invalid or expired authentication token",
                exception.getMessage()
        );
    }
}
