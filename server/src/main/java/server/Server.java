package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import dataaccess.DatabaseInitializer;
import dataaccess.UserDAO;
import handler.ClearHandler;
import io.javalin.Javalin;
import service.ClearService;
import handler.RegisterHandler;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.json.JsonMapper;
import java.lang.reflect.Type;
import handler.LoginHandler;
import handler.LogoutHandler;
import handler.ListGamesHandler;
import service.GameService;
import handler.CreateGameHandler;
import handler.JoinGameHandler;
import dataaccess.DataAccessException;

public class Server {

    private final Javalin javalin;

    public Server() {
        try {
            DatabaseInitializer.initialize();
        } catch (DataAccessException e) {
            throw new IllegalStateException("Failed to initialize database", e);
        }

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");

            Gson gson = new Gson();

            config.jsonMapper(new JsonMapper() {
                @Override
                public String toJsonString(Object obj, Type type) {
                    return gson.toJson(obj, type);
                }

                @Override
                public <T> T fromJsonString(String json, Type targetType) {
                    return gson.fromJson(json, targetType);
                }
            });
        });

        // Create the data access objects
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        // Clear service and handler
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // Registration service and handler
        UserService userService = new UserService(userDAO, authDAO);
        RegisterHandler registerHandler = new RegisterHandler(userService);

        // Login handler
        LoginHandler loginHandler = new LoginHandler(userService);

        // Logout handler
        LogoutHandler logoutHandler = new LogoutHandler(userService);

        // List games service and handler
        GameService gameService = new GameService(gameDAO, authDAO);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);

        // Create game handler
        CreateGameHandler createGameHandler = new CreateGameHandler(gameService);

        // Join game handler
        JoinGameHandler joinGameHandler = new JoinGameHandler(gameService);

        // Clear Database
        javalin.delete("/db", clearHandler::clear);

        // Register user
        javalin.post("/user", registerHandler::register);

        // Login user
        javalin.post("/session", loginHandler::login);

        // Logout user
        javalin.delete("/session", logoutHandler::logout);

        // List games
        javalin.get("/game", listGamesHandler::listGames);

        // Create game
        javalin.post("/game", createGameHandler::createGame);

        // Join game
        javalin.put("/game", joinGameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}