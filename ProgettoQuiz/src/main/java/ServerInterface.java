import java.rmi.RemoteException;

public interface ServerInterface extends java.rmi.Remote {

    boolean login(String username, ClientInterface clientRef) throws java.rmi.RemoteException;
    int createLobby(ClientInterface clientRef)  throws java.rmi.RemoteException;
    boolean joinLobby(int lobbyId, ClientInterface clientRef) throws java.rmi.RemoteException;
    void startMatch(int lobbyId) throws java.rmi.RemoteException;
    String showLobbyPlayers(int lobbyId) throws java.rmi.RemoteException;
    void sendNextQuestion(int lobbyId) throws java.rmi.RemoteException;
    void submitAnswer(String username, int lobbyId, int answerIndex, long clientTimestamp) throws RemoteException;
    void logout(ClientInterface clientRef) throws java.rmi.RemoteException;
}
