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

}
