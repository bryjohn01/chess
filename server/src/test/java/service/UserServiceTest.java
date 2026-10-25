package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void registerSuccess() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);

        UserData user = new UserData(
                "bryce",
                "password123",
                "bryce@example.com"
        );

        AuthData result = userService.register(user);

        assertEquals("bryce", result.username());
        assertNotNull(result.authToken());
        assertEquals(user, userDAO.getUser("bryce"));
        assertEquals(result, authDAO.getAuth(result.authToken()));
    }

    @Test
    public void registerDuplicateUsername() throws DataAccessException {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);

        UserData firstUser = new UserData(
                "bryce",
                "password123",
                "bryce@example.com"
        );

        UserData duplicateUser = new UserData(
                "bryce",
                "differentPassword",
                "another@example.com"
        );

        userService.register(firstUser);

        assertThrows(IllegalStateException.class, () -> {
            userService.register(duplicateUser);
        });

        assertEquals(firstUser, userDAO.getUser("bryce"));
    }

    @Test
    public void registerMissingUsername() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);

        UserData user = new UserData(
                null,
                "password123",
                "bryce@example.com"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(user);
        });
    }

    @Test
    public void registerMissingPassword() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);

        UserData user = new UserData(
                "bryce",
                null,
                "bryce@example.com"
        );

        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(user);
        });
    }

    @Test
    public void registerMissingEmail() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);

        UserData user = new UserData(
                "bryce",
                "password123",
                null
        );

        assertThrows(IllegalArgumentException.class, () -> {
            userService.register(user);
        });
    }
}