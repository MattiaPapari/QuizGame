import java.io.Serializable;

public class Player implements Serializable {
    private String username;
    private int score;
    private long lastResponseTime;

    public Player(String username, int score,  long lastResponseTime) {
        this.username = username;
        this.score = score;
        this.lastResponseTime = lastResponseTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getLastResponseTime() {
        return lastResponseTime;
    }

    public void setLastResponseTime(long lastResponseTime) {
        this.lastResponseTime = lastResponseTime;
    }
}
