package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.AuthData;
import service.UserService;

public class LoginHandler {
    private final UserService userService;

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    public void login(Context ctx) {
        try {
            LoginRequest request = ctx.bodyAsClass(LoginRequest.class);

            AuthData auth = userService.login(
                    request.username(),
                    request.password()
            );

            ctx.status(200);
            ctx.json(auth);

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));

        } catch (SecurityException e) {
            ctx.status(401);
            ctx.json(new ErrorResponse("Error: unauthorized"));

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record LoginRequest(String username, String password) {}

    private record ErrorResponse(String message) {}
}