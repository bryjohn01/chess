package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

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

        String hashedPassword = BCrypt.hashpw(
                user.password(),
                BCrypt.gensalt()
        );

        UserData secureUser = new UserData(
                user.username(),
                hashedPassword,
                user.email()
        );

        userDAO.createUser(secureUser);

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

        if (user == null || !BCrypt.checkpw(password, user.password())) {
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