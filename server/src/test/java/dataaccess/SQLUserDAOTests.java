package dataaccess;

import dataaccess.sql.SQLUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.UserData;

import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {

    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseInitializer.initialize();
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void getNonexistentUser() throws DataAccessException {
        assertNull(userDAO.getUser("nonexistent"));
    }

    @Test
    public void createAndGetUser() throws DataAccessException {
        UserData user = new UserData(
                "testuser",
                "testpassword",
                "test@example.com"
        );

        userDAO.createUser(user);

        UserData retrievedUser = userDAO.getUser("testuser");

        assertNotNull(retrievedUser);
        assertEquals(user, retrievedUser);
    }

    @Test
    public void createDuplicateUser() throws DataAccessException {
        UserData firstUser = new UserData(
                "testuser",
                "password123",
                "first@example.com"
        );

        UserData duplicateUser = new UserData(
                "testuser",
                "password456",
                "second@example.com"
        );

        userDAO.createUser(firstUser);

        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(duplicateUser);
        });
    }

    @Test
    public void clearUsers() throws DataAccessException {
        UserData user = new UserData(
                "testuser",
                "password123",
                "test@example.com"
        );

        userDAO.createUser(user);

        assertNotNull(userDAO.getUser("testuser"));

        userDAO.clear();

        assertNull(userDAO.getUser("testuser"));
    }
}