import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server extends UnicastRemoteObject implements ServerInterface {
    private String name;
    private static final String QuizServer = "quizzer";

    // Mappa: ID_Lobby -> Lista di Client per le callback
    private Map<Integer, List<ClientInterface>> lobbies = new ConcurrentHashMap<>();
    // Mappa: Client -> Dati del Giocatore (username, score)
    private Map<ClientInterface, Player> playerStats = new ConcurrentHashMap<>();
    private Map<Integer, Integer> lobbyProgress = new ConcurrentHashMap<>();
    private List<Questions> questionsList;
    private Map<Integer, Integer> answersReceived = new ConcurrentHashMap<>();
    private Map<Integer, Boolean> lobbyStarted = new ConcurrentHashMap<>();

    public Server(String s) throws RemoteException {
        super();
        name = s;

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            // Carica il file dalla cartella resources di Maven
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("Questions.json");

            if (is != null) {
                this.questionsList = mapper.readValue(is, new com.fasterxml.jackson.core.type.TypeReference<List<Questions>>(){});
                System.out.println("Server initialized with " + questionsList.size() + " questions.");
            } else {
                System.err.println("Questions.json not found in resources!");
                this.questionsList = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.questionsList = new ArrayList<>();
        }
    }

    @Override
    public boolean login(String username, ClientInterface clientRef) throws RemoteException {
        //Controlla se lo username esiste già nella mappa playerStats
        for (Player p : playerStats.values()) {
            if (p.getUsername().equals(username)) {
                clientRef.showNotification("ERROR: The username " + username + " is already in use. Please choose another.");
                return false;
            }
        }

        playerStats.put(clientRef, new Player(username, 0, 0));
        System.out.println(username + " is connected.");
        return true;
    }

    @Override
    public int createLobby(ClientInterface clientRef) throws RemoteException {
        int id = new Random().nextInt(1000);
        lobbies.put(id, new CopyOnWriteArrayList<>());
        lobbyStarted.put(id, false);

        clientRef.showNotification("Lobby created, the ID is: " + id);
        System.out.println("Lobby created, the ID is: " + id);
        return id;
    }

    @Override
    public boolean joinLobby(int lobbyId, ClientInterface clientRef) throws RemoteException {
        if (lobbies.containsKey(lobbyId)) {
            // Recuperiamo l'oggetto Player associato al riferimento remoto del client
            Player p = playerStats.get(clientRef);

            //Se la partita è già iniziata, impediamo l'accesso
            if (lobbyStarted.getOrDefault(lobbyId, false)) {
                clientRef.showNotification("Error: Match in lobby " + lobbyId + " has already started!");
                return false;
            }

            if (p != null) {
                lobbies.get(lobbyId).add(clientRef);
                clientRef.showNotification("Player '" + p.getUsername() + "' joined the lobby: " + lobbyId);
                System.out.println("Player '" + p.getUsername() + "' joined the lobby: " + lobbyId);
                return true;
            } else {
                clientRef.showNotification("Warning: A client tried to join without logging in.");
                System.out.println("Warning: A client tried to join without logging in.");
            }
        } else {
            clientRef.showNotification("Error: Lobby ID " + lobbyId + " not found.");
            System.out.println("Error: Lobby ID " + lobbyId + " not found.");
        }
        return false;
    }

    @Override
    public String showLobbyPlayers(int lobbyId) {
        List<ClientInterface> participants = lobbies.get(lobbyId);
        if (participants == null) return "Lobby not found.";

        StringBuilder sb = new StringBuilder("Players in the lobby " + lobbyId + ":\n");
        for (ClientInterface clientRef : participants) {
            Player p = playerStats.get(clientRef);
            if (p != null) {
                sb.append("- ").append(p.getUsername()).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public void sendNextQuestion(int lobbyId) throws RemoteException {
        int currentIdx = lobbyProgress.getOrDefault(lobbyId, 0);
        List<ClientInterface> participants = lobbies.get(lobbyId);

        if (questionsList != null && currentIdx < questionsList.size()) {
            Questions q = questionsList.get(currentIdx);

            for (ClientInterface client : participants) {
                //Invia il testo e opzioni.
                String username = playerStats.get(client).getUsername();
                client.receiveQuestion(q.getText(), q.getOptions(), this, username);
            }

            lobbyProgress.put(lobbyId, currentIdx + 1);
        }else {
            endGame(lobbyId);
        }
    }

    @Override
    public void startMatch(int lobbyId) throws RemoteException {
        lobbyStarted.put(lobbyId, true);
        List<ClientInterface> participants = lobbies.get(lobbyId);
        if(participants == null) return;

        participants.removeIf(c -> {
            try {
                c.showNotification("Match is starting...");
                return false;
            } catch (RemoteException e) { return true; }
        });
        for (ClientInterface c : participants) {
            try {
                c.notifyMatchStart();
            } catch (RemoteException e) {
                System.err.println("A client crashed during startup.");
            }
        }

        new Thread(() -> {
            try {
                Thread.sleep(3000); // Aspetta che i client finiscano il countdown di 3 sec
                sendNextQuestion(lobbyId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void endGame(int lobbyId) throws RemoteException {
        List<ClientInterface> participants = lobbies.get(lobbyId);

        // FINE PARTITA: Calcola e invia classifica
        StringBuilder leaderboard = new StringBuilder("\n--- FINAL RANKING ---\n");
        participants.stream()
                .map(playerStats::get)
                .sorted((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()))
                .forEach(p -> leaderboard.append(p.getUsername()).append(": ").append(p.getScore()).append(" punti\n"));

        for (ClientInterface c : participants) {
            c.showNotification(leaderboard.toString());
            c.showNotification("Thanks for playing!");
        }
    }

    @Override
    public synchronized void submitAnswer(String username, int lobbyId, int answerIndex, long clientTimestamp) throws RemoteException {
        List<ClientInterface> participants = lobbies.get(lobbyId);
        participants.removeIf(c -> {
            try { c.showNotification(""); return false; }
            catch (RemoteException e) { return true; }
        });

        //Calcolo punti e progresso
        int currentIdx = lobbyProgress.getOrDefault(lobbyId, 0) - 1;
        Questions q = questionsList.get(currentIdx);
        boolean isCorrect = (q.getCorrectIndex() == answerIndex);
        int points = calculatePoints(isCorrect, q, clientTimestamp, username);

        //Incremento contatore sincronizzato
        int received = answersReceived.getOrDefault(lobbyId, 0) + 1;
        answersReceived.put(lobbyId, received);

        //Feedback unificato
        String feedback = isCorrect ? "CORRECT! +" + points : "WRONG! Correct: " + q.getOptions().get(q.getCorrectIndex());
        // Log di debug nel server
        System.out.println("Lobby " + lobbyId + ": Answer " + received + "/" + participants.size() + " from " + username);

        for (Map.Entry<ClientInterface, Player> entry : playerStats.entrySet()) {
            if (entry.getValue().getUsername().equals(username)) {
                if (received < participants.size()) {
                    entry.getKey().showNotification(feedback + "\nWaiting for others...");
                } else {
                    entry.getKey().showNotification(feedback);
                }
                break;
            }
        }

        //Prossima domanda
        if (received >= participants.size()) {
            answersReceived.put(lobbyId, 0);
            new Thread(() -> {
                try { Thread.sleep(2000); sendNextQuestion(lobbyId); } catch (Exception e) {}
            }).start();
        }

    }


    @Override
    public void logout(ClientInterface clientRef) throws RemoteException {
        Player p = playerStats.remove(clientRef); //Rimuove dalle statistiche
        if (p != null) {
            //Rimuove il giocatore da tutte le lobby in cui era presente
            lobbies.values().forEach(list -> list.remove(clientRef));
            System.out.println("User " + p.getUsername() + " logged out and removed from lobbies.");
        }
    }

    private int calculatePoints(boolean isCorrect, Questions q, long clientTimestamp , String username){
        int points = 0;
        if (isCorrect) {
            points = q.getPoints();
            if (clientTimestamp < 5000) points += 5; // Bonus velocità
        }

        for (Player p : playerStats.values()) {
            if (p.getUsername().equals(username)) {
                p.setScore(p.getScore() + points); // Aggiungiamo i punti guadagnati
                System.out.println("DEBUG: " + username + " earned " + points + " points. Total: " + p.getScore());
                break;
            }
        }
        return points;
    }

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");

        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI registry created");

            Server obj = new Server("quizzer");
            Naming.rebind(QuizServer, obj);

            System.out.println("Server: " + QuizServer + " bound in registry");

        } catch(Exception e) {
            System.out.println("Error in server: ");
            e.printStackTrace();
        }
    }
}
