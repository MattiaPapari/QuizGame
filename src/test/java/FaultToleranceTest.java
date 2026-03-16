import java.lang.reflect.Method;
import java.rmi.RemoteException;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

class FaultToleranceTest {
    @Test
    void testCleanupDeadClient() throws Exception {
        Server server = new Server("quizzer");
        ClientInterface alive = mock(ClientInterface.class);
        ClientInterface dead = mock(ClientInterface.class);

        server.login("Alive", alive);
        server.login("Dead", dead);
        int id = server.createLobby(alive);
        server.joinLobby(id, alive);

        // FIX: Facciamo entrare il client "morto" normalmente
        server.joinLobby(id, dead);

        // SOLO ORA lo facciamo "morire" configurando il mock per lanciare eccezione
        doThrow(new RemoteException()).when(dead).showNotification(anyString());

        // Impostiamo il progresso per evitare l'Index -1 di prima
        java.lang.reflect.Field progressField = Server.class.getDeclaredField("lobbyProgress");
        progressField.setAccessible(true);
        ((java.util.Map<Integer, Integer>)progressField.get(server)).put(id, 1);

        // Quando alive risponde, il server proverà a notificare tutti e scoprirà che dead è morto
        server.submitAnswer("Alive", id, 0, 1000);

        String players = server.showLobbyPlayers(id);
        assertFalse(players.contains("Dead"), "Il client crashato deve essere rimosso dalla lista");
    }
}
