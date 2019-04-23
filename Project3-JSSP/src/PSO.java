import org.jfree.ui.RefineryUtilities;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PSO {

    public Particle globalBest;
    public Particle[] swarm;

    public PSO() {
        LookupTable table = new LookupTable();
        try {
            table.readFile(Settings.testData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        PSO test = new PSO();
        test.run();
        final long endTime = System.currentTimeMillis();
        System.out.println("Execution time: " + (endTime - startTime));
    }

    public void run() {
        // initiate swarm
        swarm = new Particle[Settings.swarmSize];
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < Settings.swarmSize; i++) {
            final int index = i;
            executorService.execute(() -> swarm[index] = new Particle());
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;

        globalBest = swarm[0];

        for (int generation = 0; generation < Settings.numberOfGenerations; generation++) {
            if (globalBest.makespan < Settings.earlyBreak && Settings.earlyStopping) {
                break;
            }
            // update global best particle
            for (Particle p : swarm) {
                if (p.makespan < globalBest.makespan) {
                    globalBest = new Particle(p);
                }
            }
            // update particle position and velocities
            updateParticles();

            if (Settings.verbose && generation % Settings.printEachGeneration == 0) {
                BA.printStatus(generation, globalBest);
            }

            if (Settings.inertiaWeight > Settings.inertiaWeightLowerBound)
                Settings.inertiaWeight = Settings.inertiaWeightHigherBound -
                        (Settings.inertiaWeightHigherBound - Settings.inertiaWeightLowerBound) / Settings.numberOfGenerations * generation;
        }

        Schedule bestSchedule = new Schedule(globalBest.particle);
        for (List<Integer> machine : bestSchedule.schedule) {
            System.out.println(machine);
        }
        BA.printStatus(Settings.numberOfGenerations, globalBest);
        plottGantt("PSO Schedule for file " + Settings.testData, bestSchedule, globalBest.makespan);


    }

    public double inertiaWeight(int iteration) {
        return (Settings.inertiaWeightHigherBound - (Settings.inertiaWeightHigherBound - Settings.inertiaWeightLowerBound) / Settings.numberOfGenerations * iteration);
    }


    private void plottGantt(String title, Schedule ganttSchedule, Double makespan) {
        final Visualizer gantt = new Visualizer(title, ganttSchedule, makespan, LookupTable.numberOfJobs);
        gantt.pack();
        RefineryUtilities.centerFrameOnScreen(gantt);
        gantt.setVisible(true);
    }

    public void updateParticles() {
        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Particle p : swarm) {
            executorService.execute(() -> p.updateParticle(globalBest));
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
    }
}
