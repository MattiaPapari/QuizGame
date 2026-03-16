import org.junit.jupiter.api.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class QuizCLITest {
    private QuizCLI ui;
    private final InputStream originalIn = System.in; // Salviamo il System.in originale

    @AfterEach
    void restoreSystemInput() {
        System.setIn(originalIn); // Ripristiniamo lo stream dopo ogni test
    }

    private void provideInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
        ui = new QuizCLI();
    }

    @Test
    @DisplayName("getUsername accetta nomi validi")
    void testGetUsernameValid() {
        provideInput("Mattia\n");
        assertEquals("Mattia", ui.getUsername());
    }

    @Test
    @DisplayName("joinLobbyExisting valida codici numerici e ignora stringhe")
    void testJoinLobbyValidation() {
        // 1. Prepariamo l'input
        provideInput("ciao\n-5\n123\n");

        // 2. Eseguiamo il metodo
        int result = ui.joinLobbyExisting();

        // 3. Verifichiamo
        assertEquals(123, result);
    }

    @Test
    @DisplayName("menu gestisce scelte fuori intervallo")
    void testMenuRange() {
        // Simuliamo: l'utente preme 5 (non esiste), poi 2 (Join Lobby)
        provideInput("5\n2\n");

        int result = ui.menu("Mattia");
        assertEquals(2, result);
    }

    @Test
    @DisplayName("startMatch accetta solo lo 0")
    void testStartMatch() {
        // Simuliamo: l'utente preme 1, poi "start", poi 0
        provideInput("1\nstart\n0\n");

        int result = ui.startMatch();
        assertEquals(0, result);
    }
}