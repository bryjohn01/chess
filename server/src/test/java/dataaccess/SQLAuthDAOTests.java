package dataaccess;

import dataaccess.sql.SQLAuthDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.AuthData;

import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {

    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        DatabaseInitializer.initialize();
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    public void getNonexistentAuth() throws DataAccessException {
        assertNull(authDAO.getAuth("nonexistent-token"));
    }

    @Test
    public void createAndGetAuth() throws DataAccessException {
        AuthData auth = new AuthData(
                "test-token-123",
                "testuser"
        );

        authDAO.createAuth(auth);

        AuthData retrievedAuth = authDAO.getAuth("test-token-123");

        assertNotNull(retrievedAuth);
        assertEquals(auth, retrievedAuth);
    }

    @Test
    public void createDuplicateAuth() throws DataAccessException {
        AuthData firstAuth = new AuthData(
                "test-token-123",
                "firstuser"
        );

        AuthData duplicateAuth = new AuthData(
                "test-token-123",
                "seconduser"
        );

        authDAO.createAuth(firstAuth);

        assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(duplicateAuth);
        });
    }

    @Test
    public void deleteAuth() throws DataAccessException {
        AuthData auth = new AuthData(
                "test-token-123",
                "testuser"
        );

        authDAO.createAuth(auth);

        assertNotNull(authDAO.getAuth("test-token-123"));

        authDAO.deleteAuth("test-token-123");

        assertNull(authDAO.getAuth("test-token-123"));
    }

    @Test
    public void clearAuth() throws DataAccessException {
        AuthData firstAuth = new AuthData(
                "token-123",
                "firstuser"
        );

        AuthData secondAuth = new AuthData(
                "token-456",
                "seconduser"
        );

        authDAO.createAuth(firstAuth);
        authDAO.createAuth(secondAuth);

        assertNotNull(authDAO.getAuth("token-123"));
        assertNotNull(authDAO.getAuth("token-456"));

        authDAO.clear();

        assertNull(authDAO.getAuth("token-123"));
        assertNull(authDAO.getAuth("token-456"));
    }
}