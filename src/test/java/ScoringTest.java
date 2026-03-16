import java.lang.reflect.Method;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ScoringTest {
    @Test
    void testCalculatePointsLogic() throws Exception {
        Server server = new Server("quizzer");
        Questions q = new Questions();
        q.setPoints(10); // Assumi setPoints esista

        Method method = Server.class.getDeclaredMethod("calculatePoints", boolean.class, Questions.class, long.class, String.class);
        method.setAccessible(true);

        // Caso 1: Corretta + Bonus Velocità (< 5000ms)
        int pointsFast = (int) method.invoke(server, true, q, 3000L, "User1");
        assertEquals(15, pointsFast);

        // Caso 2: Corretta + No Bonus (>= 5000ms)
        int pointsSlow = (int) method.invoke(server, true, q, 6000L, "User1");
        assertEquals(10, pointsSlow);

        // Caso 3: Errata
        int pointsWrong = (int) method.invoke(server, false, q, 1000L, "User1");
        assertEquals(0, pointsWrong);
    }
}