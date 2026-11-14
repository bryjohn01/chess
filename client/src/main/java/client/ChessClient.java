
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

    private boolean isLoggedIn() {
        return currentUser != null;
    }
}
