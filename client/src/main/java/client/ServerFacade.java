
package client;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
}
