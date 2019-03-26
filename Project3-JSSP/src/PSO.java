import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;

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

    public void run(){
        // initiate swarm
        swarm = new Particle[Settings.swarmSize];
        for(int i = 0; i < Settings.swarmSize; i++){
            swarm[i] = new Particle();
        }
        globalBest = swarm[0];

        for(int generation = 0; generation < Settings.numberOfGenerations; generation++){
            // update global best particle
            for(Particle p: swarm){
                if(p.makespan < globalBest.makespan){
                    globalBest = new Particle(p);
                }
            }
            // update particle position and velocities
            for(Particle p: swarm){
                p.updateParticle(globalBest);
            }
            if(Settings.verbose){
                System.out.println(" ");
                System.out.println(" ");
                System.out.println("Generation nr. "+generation);
                System.out.println("Best makespan: " + globalBest.makespan);
                System.out.println(" ");
                System.out.println(" ");
            }
        }

        Schedule bestSchedule = new Schedule(globalBest.particle);
        for(List<Integer> machine: bestSchedule.schedule){
            System.out.println(machine);
        }

    }

    public static void main(String[] args) {
        PSO test = new PSO();
        test.run();
    }
}
