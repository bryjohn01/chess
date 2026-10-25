package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (user == null ||
                user.username() == null || user.username().isBlank() ||
                user.password() == null || user.password().isBlank() ||
                user.email() == null || user.email().isBlank()) {
            throw new IllegalArgumentException("Error: bad request");
        }

        if (userDAO.getUser(user.username()) != null) {
            throw new IllegalStateException("Error: already taken");
        }

        userDAO.createUser(user);

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, user.username());

        authDAO.createAuth(auth);

        return auth;
    }

    public AuthData login(String username, String password)
            throws DataAccessException {

        if (username == null || username.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("Error: bad request");
        }

        UserData user = userDAO.getUser(username);

        if (user == null || !user.password().equals(password)) {
            throw new SecurityException("Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);

        authDAO.createAuth(auth);

        return auth;
    }

    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || authDAO.getAuth(authToken) == null) {
            throw new SecurityException("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}