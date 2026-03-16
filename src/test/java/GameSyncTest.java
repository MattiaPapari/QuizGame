import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameSyncTest {
    @Test
    void testBarrierFlow() throws Exception {
        Server server = new Server("quizzer");
        ClientInterface p1 = mock(ClientInterface.class);
        ClientInterface p2 = mock(ClientInterface.class);
        server.login("P1", p1);
        server.login("P2", p2);

        int id = server.createLobby(p1);
        server.joinLobby(id, p1);
        server.joinLobby(id, p2);

        // FIX: Impostiamo il progresso a 1 (come se la prima domanda fosse stata inviata)
        java.lang.reflect.Field progressField = Server.class.getDeclaredField("lobbyProgress");
        progressField.setAccessible(true);
        ((java.util.Map<Integer, Integer>)progressField.get(server)).put(id, 1);

        // Ora submitAnswer leggerà (1 - 1) = indice 0 (Corretto!)
        server.submitAnswer("P1", id, 0, 1000);

        java.lang.reflect.Field answersField = Server.class.getDeclaredField("answersReceived");
        answersField.setAccessible(true);
        java.util.Map<Integer, Integer> map = (java.util.Map) answersField.get(server);

        assertEquals(1, map.get(id));
        server.submitAnswer("P2", id, 0, 1000);
        assertEquals(0, map.get(id));
    }
}