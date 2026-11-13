
package client;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ServerFacade {

    private final String serverUrl;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    public AuthData register(String username, String password, String email)
            throws Exception {

        UserData user = new UserData(username, password, email);
        String requestBody = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();


        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        }

        throw new Exception(
                "Registration failed: " +
                        getErrorMessage(response.statusCode())
        );
    }

    public AuthData login(String username, String password)
            throws Exception {

        record LoginRequest(String username, String password) {}

        LoginRequest loginRequest = new LoginRequest(username, password);
        String requestBody = gson.toJson(loginRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), AuthData.class);
        }

        String message = switch (response.statusCode()) {
            case 400 -> "Invalid login information";
            case 401 -> "Incorrect username or password";
            default -> "An unexpected server error occurred";
        };

        throw new Exception("Login failed: " + message);
    }

    public void logout(String authToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/session"))
                .header("Authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 200) {
            return;
        }

        String message = switch (response.statusCode()) {
            case 401 -> "Invalid or expired authentication token";
            default -> "An unexpected server error occurred";
        };

        throw new Exception("Logout failed: " + message);
    }

    public int createGame(String authToken, String gameName)
            throws Exception {
        record CreateGameRequest(String gameName) {}
        record CreateGameResponse(int gameID) {}

        CreateGameRequest createRequest = new CreateGameRequest(gameName);
        String requestBody = gson.toJson(createRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 200) {
            CreateGameResponse gameResponse =
                    gson.fromJson(response.body(), CreateGameResponse.class);
            return gameResponse.gameID();
        }

        String message = switch (response.statusCode()) {
            case 400 -> "Invalid game name";
            case 401 -> "Invalid or expired authentication token";
            default -> "An unexpected server error occurred";
        };

        throw new Exception("Create game failed: " + message);
    }

    public List<GameSummary> listGames(String authToken)
            throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 200) {
            ListGamesResponse gamesResponse =
                    gson.fromJson(response.body(), ListGamesResponse.class);
            return gamesResponse.games();
        }

        String message = switch (response.statusCode()) {
            case 401 -> "Invalid or expired authentication token";
            default -> "An unexpected server error occurred";
        };

        throw new Exception("List games failed: " + message);
    }

    public void joinGame(String authToken, String playerColor, int gameID)
            throws Exception {
        record JoinGameRequest(String playerColor, int gameID) {}

        JoinGameRequest joinRequest =
                new JoinGameRequest(playerColor, gameID);

        String requestBody = gson.toJson(joinRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + "/game"))
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = sendRequest(request);

        if (response.statusCode() == 200) {
            return;
        }

        String message = switch (response.statusCode()) {
            case 400 -> "Invalid game ID or player color";
            case 401 -> "Invalid or expired authentication token";
            case 403 -> "Selected player position is already taken";
            default -> "An unexpected server error occurred";
        };

        throw new Exception("Join game failed: " + message);
    }

    private HttpResponse<String> sendRequest(HttpRequest request)
            throws Exception {

        try {
            return httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
        } catch (IOException e) {
            throw new Exception(
                    "Unable to connect to the chess server", e
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception(
                    "Request was interrupted", e
            );
        }
    }

    private String getErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Invalid registration information";
            case 403 -> "Username is already taken";
            default -> "An unexpected server error occurred";
        };
    }

    public record GameSummary(
            int gameID,
            String whiteUsername,
            String blackUsername,
            String gameName
    ) {}

    private record ListGamesResponse(
            List<GameSummary> games
    ) {}
}
