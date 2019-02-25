import java.awt.*;

public class Edge {
    private Pixel from;
    private Pixel to;

    private double distance;

    public Edge(Pixel from, Pixel to) {
        this.from = from;
        this.to = to;
        this.distance = dist(from, to);
    }

    //Calculates the Euclidean distance by using RGB
    public static double dist(Pixel FromPixel, Pixel ToPixel) {
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
