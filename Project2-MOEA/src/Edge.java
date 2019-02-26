import java.awt.*;
import java.io.FileReader;

public class Edge implements Comparable<Edge>{
    private int from;
    private int to;

    private double distance;

    public Edge(Pixel from, Pixel to) {
        this.from = from.getPixelIdx();
        this.to = to.getPixelIdx();
        this.distance = dist(from, to);
    }

    //Calculates the Euclidean distance by using RGB
    public static double dist(Pixel FromPixel, Pixel ToPixel) {
        double deltaRed = FromPixel.getRed() - ToPixel.getRed();
        double deltaGreen = FromPixel.getGreen() - ToPixel.getGreen();
        double deltaBlue = FromPixel.getBlue() - ToPixel.getBlue();
        return Math.sqrt((Math.pow(deltaRed, 2)) + (Math.pow(deltaGreen, 2)) + (Math.pow(deltaBlue, 2)));
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Edge o) {
        if (this.getDistance() > o.getDistance()) return 1;
        if (this.getDistance() < o.getDistance()) return -1;
        return 0;
    }
}
