import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.rmi.RemoteException;

class LobbyTest {
    private Server server;
    private ClientInterface client;

    @BeforeEach
    void setUp() throws RemoteException {
        server = new Server("quizzer");
        client = mock(ClientInterface.class);
        server.login("Player", client);
    }

    @Test
    void testCreateAndJoinLobby() throws RemoteException {
        int id = server.createLobby(client);
        assertTrue(id >= 0 && id < 1000);
        assertTrue(server.joinLobby(id, client));
    }

    @Test
    void testJoinInvalidLobby() throws RemoteException {
        assertFalse(server.joinLobby(9999, client));
        verify(client).showNotification(contains("not found"));
    }
}