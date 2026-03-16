import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.rmi.RemoteException;

class AuthTest {
    private Server server;
    private ClientInterface mockClient;

    @BeforeEach
    void setUp() throws RemoteException {
        server = new Server("quizzer");
        mockClient = mock(ClientInterface.class);
    }

    @Test
    @DisplayName("Login con successo")
    void testLoginSuccess() throws RemoteException {
        assertTrue(server.login("User1", mockClient));
    }

    @Test
    @DisplayName("Rifiuto username duplicato")
    void testLoginDuplicate() throws RemoteException {
        server.login("User1", mockClient);
        ClientInterface secondClient = mock(ClientInterface.class);
        assertFalse(server.login("User1", secondClient));
        verify(secondClient).showNotification(contains("already in use"));
    }

    @Test
    @DisplayName("Logout libera lo username")
    void testLogoutCleanup() throws RemoteException {
        server.login("User1", mockClient);
        server.logout(mockClient);
        // Ora User1 dovrebbe potersi loggare di nuovo
        assertTrue(server.login("User1", mockClient));
    }
}