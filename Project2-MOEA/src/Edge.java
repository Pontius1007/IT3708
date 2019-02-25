import java.awt.*;

public class Edge {
    private Pixel from;
    private Pixel to;

    private double distance;

    public Edge(Pixel from, Pixel to, int colFromIdx, int colToIdx, int rowFromIdx, int rowToIdx) {
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

    public double getDistance() {
        return distance;
    }
}
