import java.util.List;
import java.util.Scanner;

public class QuizCLI {
    private final Scanner scanner = new Scanner(System.in);

    public String getUsername() {
        String username = "";
        while (username == null || username.trim().isEmpty()) {
            printSpace();
            System.out.print("Enter your username: ");
            username = scanner.nextLine();

            if (username == null || username.trim().isEmpty()) {
                System.out.println("Invalid username. Field cannot be empty.");
            }
        }
        return username;
    }

    public void printSpace(){
        System.out.println("\n========================");
    }

    public int joinLobbyExisting() {
        int lobbyCode = -1;

        while (lobbyCode < 0) {
            printSpace();
            System.out.print("Enter the lobby's code to join: ");

            if (scanner.hasNextInt()) {
                lobbyCode = scanner.nextInt();
                if (scanner.hasNextLine()) scanner.nextLine();

                if (lobbyCode < 0) {
                    System.out.println("Error: Lobby code must be a positive number.");
                }
            } else {
                String inputErrato = scanner.next();
                System.out.println("Error: '" + inputErrato + "' is not a valid code. Please enter only numbers.");
            }
        }
        return lobbyCode;
    }

    public int menu(String username) {
        int choice = -1;
        while (choice < 1 || choice > 3) {
            printSpace();
            System.out.println("Welcome to the game " + username + "!");
            printSpace();
            System.out.println("Enter the corresponding number: ");
            System.out.println("1. Create a lobby");
            System.out.println("2. Join a lobby");
            System.out.println("3. Exit");
            System.out.print("Choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (scanner.hasNextLine()) scanner.nextLine();

                if (choice < 1 || choice > 3) {
                    System.out.println("Invalid choice. Please enter 1, 2 or 3.");
                }
            } else {
                String inputErrato = scanner.next();
                System.out.println("Error: '" + inputErrato + "' is not a number.");
            }
        }
        return choice;
    }

    public int startMatch(){
        int choice = -1;
        while (choice != 0) {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                if (scanner.hasNextLine()) scanner.nextLine();

                if (choice != 0) {
                    System.out.println("Error. Please enter 0 to start the match.");
                }
            } else {
                String inputErrato = scanner.next();
                System.out.println("Error: '" + inputErrato + "' is invalid. Please enter 0 to start the match.");
            }
        }
        return choice;
    }

    public void displayTitle() {
        System.out.println("******************************************");
        System.out.println("*     WELCOME TO THE QUIZ GAME!     *");
        System.out.println("*     Challenge your friends online!     *");
        System.out.println("******************************************");
    }
}
