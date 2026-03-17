import java.rmi.*;
import java.util.List;

public interface ClientInterface extends java.rmi.Remote {
    void notifyMatchStart() throws RemoteException;
    void receiveQuestion(String questionText, List<String> options, ServerInterface obj_server, String username) throws RemoteException;
    void showNotification(String message) throws RemoteException;
}
