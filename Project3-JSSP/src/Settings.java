import java.util.HashMap;
import java.util.Map;


public class Settings {
    // acceptable values for test data
    public static Map<String, Integer> acceptable = new HashMap<>(){{
        put("1", 56);
        put("2", 1059);
        put("3", 1276);
        put("4", 1130);
        put("5", 1451);
        put("6", 979);
    }};

    // init
    public static final double xmin = 0;
    public static final double xmax = 4;
    public static final double vmin = -4;
    public static final double vmax = 4;


    // exploration exploitation
    public static final double c1 = 2;
    public static final double c2 = 2;
    public static double inertiaWeight = 0.9;
    public static final double inertiaWeightDecrementFactor = 0.975;
    public static final double inertiaWeightLowerBound = 0.4;


    // PSO parameters
    public static final int swarmSize = 400;
    public static final int numberOfGenerations = 3000;
    public static final String testData = "6";
    public static final boolean earlyStopping = true;
    public static final boolean verbose = true;


    // Helping parameters
    public static final double earlyBreak = (1.1 * Settings.acceptable.get(testData));

}
