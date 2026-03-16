import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.rmi.RemoteException;

class ClientStateTest {
    private Client client;

    @BeforeEach
    void setUp() throws RemoteException {
        client = new Client();
    }

    @Test
    void testNotifyMatchStartChangesFlag() throws RemoteException {
        assertFalse(client.hasMatchStarted());
        client.notifyMatchStart(); // Simula chiamata dal server
        assertTrue(client.hasMatchStarted());
    }
}