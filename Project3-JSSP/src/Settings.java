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
    public static final double inertiaWeightDecrementFactor = 0.975;
    public static final double inertiaWeightLowerBound = 0.4;


    // PSO parameters
    public static final int swarmSize = 40;



    // ************************* BA PARAMETERS *****************************


    public static final int numberOfScoutBees = 500; // n
    public static final int numberOfBestPatches = 150; // m
    public static final int numberOfElitePatches = 30; // e
    public static final int nRecruitedBeesElite = 120; // nep
    public static final int nRecruitedBeesNonElite = 80; // nsp
    public static final int neighbourhoodSize = 1; // ngh
    public static final double differenceFistAndLastIteration = 0.001;
    public static final int shrinkingConstant = 2; // sc
    public static final int repetitionsForShrinking = 10; // ngh
    public static final int repetitionsForEnhancements = 25; // ngh
    public static final int repetitionsForSiteAbandonment = 100; // ngh


    // ************************* SHARED PARAMETERS ***************************

    // acceptable values for test data
    public static Map<String, Integer> acceptable = new HashMap<>(){{
        put("1", 56);
        put("2", 1059);
        put("3", 1276);
        put("4", 1130);
        put("5", 1451);
        put("6", 979);
    }};

    public static final int numberOfGenerations = 100000; // ma
    public static final String testData = "2";
    public static final boolean earlyStopping = true;
    public static final boolean verbose = true;

    // Helping parameters
    public static final double earlyBreak = (1.1 * Settings.acceptable.get(testData));
}
