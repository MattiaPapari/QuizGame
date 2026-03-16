import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

class ClientGameLogicTest {
    private Client client;
    private ServerInterface mockServer;

    @BeforeEach
    void setUp() throws RemoteException {
        client = new Client();
        mockServer = mock(ServerInterface.class);
        client.setUsername("Tester");
        client.setCurrentLobbyId(123);
    }

    @Test
    @DisplayName("Ricezione domanda e invio risposta (Simulata)")
    void testReceiveQuestionAndSubmit() throws Exception {
        List<String> options = Arrays.asList("Opzione A", "Opzione B", "Opzione C");

        // Simula l'utente che preme "1" e invia l'input allo System.in
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Chiamata al metodo
        client.receiveQuestion("Qual è il test?", options, mockServer, "Tester");

        // Poiché receiveQuestion lancia un Thread, aspettiamo un istante
        Thread.sleep(500);

        // Verifica che il server abbia ricevuto la chiamata submitAnswer
        // Non verifichiamo l'indice esatto perché c'è lo shuffle, ma verifichiamo la chiamata
        verify(mockServer, timeout(1000)).submitAnswer(eq("Tester"), eq(123), anyInt(), anyLong());
    }
}