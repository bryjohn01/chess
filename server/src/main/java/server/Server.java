package server;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
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

public class Server {

    private final Javalin javalin;

    public Server() {
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
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        // Clear service and handler
        ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);

        // Registration service and handler
        UserService userService = new UserService(userDAO, authDAO);
        RegisterHandler registerHandler = new RegisterHandler(userService);

        // Login service and handler
        LoginHandler loginHandler = new LoginHandler(userService);

        // Logout service and handler
        LogoutHandler logoutHandler = new LogoutHandler(userService);

        // List games service and handler
        GameService gameService = new GameService(gameDAO, authDAO);
        ListGamesHandler listGamesHandler = new ListGamesHandler(gameService);

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
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}