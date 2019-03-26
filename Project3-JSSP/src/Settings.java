public class Settings {
    // init
    public static final double xmin = 0;
    public static final double max = 4;
    public static final double vmin = -4;
    public static final double vmax = 4;


    // exploration exploitation
    public static final double c1 = 2;
    public static final double c2 = 2;
    public static double inertiaWeight = 0.9;
    public static final double inertiaWeightDecrementFactor = 0.975;
    public static final double inertiaWeightLowerBound = 0.4;

    // PSO parameters
    public static final int swarmSize = 500;
    public static final int numberOfGenerations = 600;
    public static final String testData = "1";

    public static final boolean verbose = true;

}
