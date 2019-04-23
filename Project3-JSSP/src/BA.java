import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.SplittableRandom;

public class BA {

    Bee[] scoutBees;
    Bee queenBee;
    int endGeneration;


    public BA() {
        LookupTable table = new LookupTable();
        try {
            table.readFile(Settings.testData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        // initiate scout bees (Step 1)
        scoutBees = new Bee[Settings.numberOfScoutBees];
        for (int i = 0; i < scoutBees.length; i++) {
            scoutBees[i] = new Bee();
        }

        // Step 2
        Arrays.sort(scoutBees);
        queenBee = new Bee(scoutBees[0]);

        for (int generation = 0; generation < Settings.numberOfGenerations; generation++) {
            if (generation % Settings.printEachGeneration == 0 && Settings.verbose) {
                printStatus(generation, queenBee);
            }

            if (termination()) {
                endGeneration = generation;
                break;
            }

            // Local Search

            // Abandon patch if stuck for N repetitions
            for(int i = 0; i < Settings.numberOfBestPatches; i++){
                if (++scoutBees[i].repetitions > Settings.repetitionsForSiteAbandonment) {
                    scoutBees[i] = new Bee();
                }
            }

            // find best solution from each elite patch
            for (int i = 0; i < Settings.numberOfElitePatches; i++) {
                Bee scoutBee = scoutBees[i];
                for (int j = 0; j < Settings.nRecruitedBeesElite; j++) {
                    Bee recruit = new Bee(scoutBee, true);
                    if (recruit.makespan < scoutBees[i].makespan) {
                        scoutBees[i] = recruit;
                    }
                }
            }

            // find best solution from non elite patch
            for (int i = Settings.numberOfElitePatches; i < Settings.numberOfBestPatches; i++) {
                Bee scoutBee = scoutBees[i];
                for (int j = 0; j < Settings.nRecruitedBeesNonElite; j++) {
                    Bee recruit = new Bee(scoutBee, true);
                    if (recruit.makespan < scoutBees[i].makespan) {
                        scoutBees[i] = recruit;
                    }
                }
            }

            if (termination()) {
                endGeneration = generation;
                break;
            }
            // Global Search

            for (int i = Settings.numberOfBestPatches; i < scoutBees.length; i++) {
                scoutBees[i] = new Bee();
            }

        }

        // print final best solution
        System.out.println(" ");
        System.out.println(" ");
        System.out.println("Final Solution");
        System.out.println("Final makespan: " + queenBee.makespan);
        System.out.println("Final generation: " + endGeneration);
        System.out.println(" ");
        System.out.println(" ");
        Schedule bestSchedule = new Schedule(queenBee.particle);
        PSO.plottGantt("BA Schedule for file " + Settings.testData, bestSchedule, queenBee.makespan);
        for (List<Integer> machine : bestSchedule.schedule) {
            System.out.println(machine);
        }
    }

    static void printStatus(int generation, Particle queenBee) {
        if (Settings.verbose) {
            System.out.println(" ");
            System.out.println(" ");
            System.out.println("Generation nr. " + generation);
            System.out.println("Best makespan: " + queenBee.makespan);
            System.out.println(" ");
            System.out.println(" ");
        }
    }

    public boolean termination() {
        // step 2
        Arrays.sort(scoutBees);
        // step 3
        if (scoutBees[0].makespan < queenBee.makespan) {
            queenBee = new Bee(scoutBees[0]);
        }
        if (!Settings.earlyStopping || queenBee.makespan > Settings.earlyBreak){
            return false;
        }
        return true;
    }

    public static void main(String[] args) {

        final long startTime = System.currentTimeMillis();
        BA test = new BA();
        test.run();
        final long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime));

    }
}
