import java.io.Serializable;
import java.util.List;

public class Questions  implements Serializable {
    public int id;
    public String text;
    public List<String> options;
    public int correctIndex;
    public int points;

    public Questions() {
    }

    public Questions(int id, String text, List<String> options, int correctIndex, int points) {
        this.id = id;
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    public void setCorrectIndex(int correctIndex) {
        this.correctIndex = correctIndex;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
