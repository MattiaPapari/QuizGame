import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private int currentLobbyId;
    private String username;
    private volatile boolean matchStarted = false;

    protected Client() throws RemoteException {
        super();
    }

    public void setCurrentLobbyId(int currentLobbyId) throws RemoteException {
        this.currentLobbyId = currentLobbyId;
    }
    public void setUsername(String name) { this.username = name; }

    @Override
    public void notifyMatchStart() throws RemoteException {
        this.matchStarted = true;
        System.out.println("Match will start in: ");

        try {
            for (int i = 3; i > 0; i--) {
                System.out.println(i);
                System.out.flush();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.err.println("Local countdown error.");
            Thread.currentThread().interrupt();
        }

        System.out.println("START");
    }

    public boolean hasMatchStarted(){ return matchStarted;}

    @Override
    public void receiveQuestion(String questionText, List<String> options, ServerInterface obj_server, String username) throws RemoteException {
        // Creiamo un nuovo thread per gestire la domanda.
        // Questo permette al metodo RMI di finire subito e sbloccare il Server.
        new Thread(() -> {
            try {
                //Rimescolamento locale
                List<String> shuffledOptions = new ArrayList<>(options);
                Collections.shuffle(shuffledOptions);

                long startTime = System.currentTimeMillis();

                //Visualizzazione
                System.out.println("\n========================");
                System.out.println("QUESTION: " + questionText);
                for (int i = 0; i < shuffledOptions.size(); i++) {
                    System.out.println((i + 1) + ". " + shuffledOptions.get(i));
                }

                //Acquisizione risposta (Bloccante per l'utente, ma non per RMI)
                Scanner sc = new Scanner(System.in);
                int userChoice = -1;
                while (userChoice < 1 || userChoice > shuffledOptions.size()) {
                    System.out.print("Choose correct answer (1-" + shuffledOptions.size() + "): ");
                    if (sc.hasNextInt()) {
                        userChoice = sc.nextInt();
                    } else {
                        sc.next();
                    }
                }

                long endTime = System.currentTimeMillis();
                long reactionTime = endTime - startTime;

                String selectedText = shuffledOptions.get(userChoice - 1);
                int originalIndex = options.indexOf(selectedText);

                //INVIO RISPOSTA
                obj_server.submitAnswer(this.username, this.currentLobbyId, originalIndex, reactionTime);

            } catch (Exception e) {
                System.err.println("Error while handling local question.");
                e.printStackTrace();
            }
        }).start(); // Avvia il thread e libera immediatamente il server
    }

    @Override
    public void showNotification(String message) throws RemoteException {
        System.out.println(message);
    }


    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        String registryURL = "rmi://127.0.0.1/quizzer";
        QuizCLI ui = new QuizCLI();
        int idLobby = 0;

        try {
            System.out.println("Connection to: " + registryURL + "\n");
            ServerInterface obj_server = (ServerInterface) Naming.lookup(registryURL);

            ui.displayTitle();

            Client client = new Client();
            //Richiesta nome utente
            String username = "";
            boolean loginSuccess = false;

            while (!loginSuccess) {
                username = ui.getUsername();
                try {
                    loginSuccess = obj_server.login(username, client);
                }catch (RemoteException e) {
                    System.err.println("Connection error during login");
                    break;
                }
            }

            client.setUsername(username);
            boolean checkLobby = false;
            do {
                int choice = ui.menu(username);
                switch (choice) {
                    case 1:
                        ui.printSpace();
                        //Richiesta creazione lobby + join alla lobby creata
                        idLobby = obj_server.createLobby(client);
                        ui.printSpace();
                        checkLobby =  obj_server.joinLobby(idLobby, client);
                        if (checkLobby) {
                            client.setCurrentLobbyId(idLobby);
                        }
                        break;
                    case 2:
                        idLobby = ui.joinLobbyExisting();
                        ui.printSpace();
                        //Inserimento giocatore alla lobby
                        checkLobby = obj_server.joinLobby(idLobby, client);
                        if (checkLobby) {
                            client.setCurrentLobbyId(idLobby);
                        }else {
                            System.out.println("Could not join. Returning to menu...");
                        }
                        break;
                    case 3:
                        System.out.println("Logging out...");
                        obj_server.logout(client);
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                }
            }while (!checkLobby);

            //Mostra lista giocatori nella lobby
            ui.printSpace();
            String list = obj_server.showLobbyPlayers(idLobby);
            System.out.println(list);

            ui.printSpace();
            System.out.println("Wait for host or enter 0 to start:");

            while (!client.hasMatchStarted()) {
                if (System.in.available() > 0) {
                    int s_match = ui.startMatch();
                    if (s_match == 0 && !client.hasMatchStarted()) {
                        obj_server.startMatch(idLobby);
                        break;
                    }
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
            System.out.println("Error in client: ");
            e.printStackTrace();
        }
    }
}
