import java.util.HashMap;
import java.util.Map;


public class Settings {

    // *************************** PSO PARAMETERS ************************
    // init
    public static final double xmin = 0;
    public static final double xmax = 4;
    public static final double vmin = -4;
    public static final double vmax = 4;


    // exploration exploitation
    public static final double c1 = 2;
    public static final double c2 = 2;
    public static double inertiaWeight = 0.9;
    public static final double inertiaWeightHigherBound = 0.6;
    public static final double inertiaWeightLowerBound = 0.2;


    // PSO parameters
    public static final int swarmSize = 1000;


    // ************************* BA PARAMETERS *****************************


    public static final int numberOfScoutBees = 50; // n
    public static final int numberOfBestPatches = 15; // m
    public static final int numberOfElitePatches = 3; // e
    public static final int nRecruitedBeesElite = 12; // nep
    public static final int nRecruitedBeesNonElite = 8; // nsp
    //
    public static final int repetitionsForSiteAbandonment = 100; // ngh


    // ************************* SHARED PARAMETERS ***************************

    // acceptable values for test data
    public static Map<String, Integer> acceptable = new HashMap<>() {{
        put("1", 56);
        put("2", 1059);
        put("3", 1276);
        put("4", 1130);
        put("5", 1451);
        put("6", 979);
    }};

    public static final int numberOfGenerations = 10000; // ma
    public static final String testData = "5";
    public static final boolean earlyStopping = true;
    public static final boolean verbose = true;
    public static final int printEachGeneration = 50;

    // Helping parameters
    public static final double earlyBreak = (1.1 * Settings.acceptable.get(testData));
}
