import java.awt.*;

public class Edge {
    private Color from;
    private Color to;
    private double distance;

    public Edge(Color from, Color to, float distance) {
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public Color getFrom() {
        return from;
    }

    public Color getTo() {
        return to;
    }

    public float getDistance() {
        return distance;
    }
}
