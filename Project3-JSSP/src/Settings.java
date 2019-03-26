public class Settings {
    // init
    public static final double xmin = 0;
    public static final double max = 2;
    public static final double vmin = -2;
    public static final double vmax = 2;


    // exploration exploitation
    public static final double c1 = 1;
    public static final double c2 = 1;
    public static final double inertiaWeight = 0.8;
    public static final double getInertiaWeightDecrementFactor = 0.1;

    // PSO parameters
    public static final int swarmSize = 40;
    public static final int numberOfGenerations = 10000;
    public static final String testData = "1";

    public static final boolean verbose = true;

}
