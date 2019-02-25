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

    //Calculates the Euclidean distance by using RGB
    public double dist(Color FromPixel, Color ToPixel) {
        double deltaRed = FromPixel.getRed() - ToPixel.getRed();
        double deltaGreen = FromPixel.getGreen() - ToPixel.getGreen();
        double deltaBlue = FromPixel.getBlue() - ToPixel.getBlue();
        return Math.sqrt((Math.pow(deltaRed, 2)) + (Math.pow(deltaGreen, 2)) + (Math.pow(deltaBlue, 2)));
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
