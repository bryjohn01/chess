package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.UserService;

public class LogoutHandler {
    private final UserService userService;

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");

            userService.logout(authToken);

            ctx.status(200);
            ctx.json(new EmptyResponse());

        } catch (SecurityException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record EmptyResponse() {}

    private record ErrorResponse(String message) {}
}