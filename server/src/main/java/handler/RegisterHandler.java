package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.AuthData;
import model.UserData;
import service.UserService;

public class RegisterHandler {
    private final UserService userService;

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        try {
            UserData user = ctx.bodyAsClass(UserData.class);
            AuthData auth = userService.register(user);

            ctx.status(200);
            ctx.json(auth);

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.json(new ErrorResponse("Error: bad request"));

        } catch (IllegalStateException e) {
            ctx.status(403);
            ctx.json(new ErrorResponse("Error: already taken"));

        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(new ErrorResponse("Error: " + e.getMessage()));
        }
    }

    private record ErrorResponse(String message) {
    }
}