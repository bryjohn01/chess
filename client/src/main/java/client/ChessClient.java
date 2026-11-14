
package client;

import java.util.Scanner;
import model.AuthData;

public class ChessClient {

    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    private final ServerFacade facade = new ServerFacade(8080);
    private AuthData currentUser;

    public void run() {
        System.out.println("♕ Welcome to CS 240 Chess!");
        System.out.println("Type 'help' to see available commands.");

        while (running) {
            String state = isLoggedIn() ? "POSTLOGIN" : "PRELOGIN";
            System.out.print("\n[" + state + "] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();


            switch (input) {
                case "help" -> showHelp();

                case "quit" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }

                case "register" -> {
                    if (!isLoggedIn()) {
                        register();
                    } else {
                        System.out.println("You are already logged in.");
                    }
                }

                case "login" -> {
                    if (!isLoggedIn()) {
                        login();
                    } else {
                        System.out.println("You are already logged in.");
                    }
                }

                case "logout" -> {
                    if (isLoggedIn()) {
                        logout();
                    } else {
                        System.out.println("You must be logged in to use this command.");
                    }
                }

                case "create" -> {
                    if (isLoggedIn()) {
                        createGame();
                    } else {
                        System.out.println("You must be logged in to create a game.");
                    }
                }

                case "list" -> {
                    if (isLoggedIn()) {
                        listGames();
                    } else {
                        System.out.println("You must be logged in to list games.");
                    }
                }

                case "play" -> {
                    if (isLoggedIn()) {
                        playGame();
                    } else {
                        System.out.println("You must be logged in to play a game.");
                    }
                }

                default -> System.out.println(
                        "Unknown command. Type 'help' for available commands."
                );
            }

        }
    }

    private void showHelp() {
        if (isLoggedIn()) {
            System.out.println("""
                Available commands:
                  help    - Show available commands
                  logout  - Log out of your account
                  create  - Create a new chess game
                  list    - List available chess games
                  play    - Join a chess game as a player
                  observe - Observe a chess game
                """);
        } else {
            System.out.println("""
                Available commands:
                  help     - Show available commands
                  quit     - Exit the application
                  login    - Log in to your account
                  register - Create a new account
                """);
        }
    }

    private void register() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            currentUser = facade.register(username, password, email);

            System.out.println("Registration successful!");
            System.out.println("Welcome, " + currentUser.username() + "!");

        } catch (Exception e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private void login() {
        try {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            currentUser = facade.login(username, password);

            System.out.println("Login successful!");
            System.out.println("Welcome back, " + currentUser.username() + "!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void logout() {
        try {
            facade.logout(currentUser.authToken());

            String username = currentUser.username();
            currentUser = null;

            System.out.println("Logout successful!");
            System.out.println("Goodbye, " + username + "!");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void createGame() {
        try {
            System.out.print("Game name: ");
            String gameName = scanner.nextLine().trim();

            if (gameName.isEmpty()) {
                System.out.println("Game name cannot be empty.");
                return;
            }

            facade.createGame(currentUser.authToken(), gameName);

            System.out.println("Game created successfully!");
            System.out.println("Game name: " + gameName);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void listGames() {
        try {
            var games = facade.listGames(currentUser.authToken());

            if (games.isEmpty()) {
                System.out.println("No chess games available.");
                return;
            }

            System.out.println("\nAvailable Chess Games:");

            for (int i = 0; i < games.size(); i++) {
                var game = games.get(i);

                String whitePlayer = game.whiteUsername() == null
                        ? "Available"
                        : game.whiteUsername();

                String blackPlayer = game.blackUsername() == null
                        ? "Available"
                        : game.blackUsername();

                System.out.println((i + 1) + ". " + game.gameName());
                System.out.println("   White: " + whitePlayer);
                System.out.println("   Black: " + blackPlayer);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void playGame() {
        try {
            var games = facade.listGames(currentUser.authToken());

            if (games.isEmpty()) {
                System.out.println("No chess games available.");
                return;
            }

            System.out.println("\nSelect a Chess Game:");

            for (int i = 0; i < games.size(); i++) {
                System.out.println(
                        (i + 1) + ". " + games.get(i).gameName()
                );
            }

            System.out.print("\nEnter game number: ");
            String selection = scanner.nextLine().trim();

            int gameNumber;

            try {
                gameNumber = Integer.parseInt(selection);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid game number.");
                return;
            }

            if (gameNumber < 1 || gameNumber > games.size()) {
                System.out.println("Game number is out of range.");
                return;
            }

            var selectedGame = games.get(gameNumber - 1);

            System.out.println("Selected game: " + selectedGame.gameName());

            System.out.print("Choose your color (WHITE/BLACK): ");
            String playerColor = scanner.nextLine().trim().toUpperCase();

            if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                System.out.println("Invalid color. Please choose WHITE or BLACK.");
                return;
            }

            facade.joinGame(
                    currentUser.authToken(),
                    playerColor,
                    selectedGame.gameID()
            );

            System.out.println("Successfully joined the game!");
            System.out.println("Game: " + selectedGame.gameName());
            System.out.println("Playing as: " + playerColor);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean isLoggedIn() {
        return currentUser != null;
    }
}
