import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Particle implements Comparable<Particle> {

    public Operation[] particle;
    public double makespan;

    public Particle localBest;

    // copying particle constructor
    public Particle(Particle cloneParticle) {
        particle = new Operation[LookupTable.numberOfJobs * LookupTable.numberOfMachines];
        for (int job = 0; job < LookupTable.numberOfJobs * LookupTable.numberOfMachines; job++) {
            Operation cloneOperation = cloneParticle.particle[job];
            particle[job] = new Operation(cloneOperation.jobId, cloneOperation.position, cloneOperation.velocity);
        }
        this.makespan = cloneParticle.makespan;
    }

    public Particle() {
        particle = new Operation[LookupTable.numberOfJobs * LookupTable.numberOfMachines];
        // adds all the operations to the particle with random position and velocity vectors
        for (int jobIdx = 0; jobIdx < LookupTable.numberOfJobs; jobIdx++) {
            for (int machineIdx = 0; machineIdx < LookupTable.numberOfMachines; machineIdx++) {
                particle[jobIdx * LookupTable.numberOfMachines + machineIdx] = new Operation(jobIdx,
                        getRandomDoubleBetweenRange(Settings.xmin, Settings.xmax),
                        getRandomDoubleBetweenRange(Settings.vmin, Settings.vmax));
            }
        }
        this.localBest = new Particle(this);
        updateMakespan();
    }

    public static double getRandomDoubleBetweenRange(double min, double max) {
        return (Math.random() * ((max - min))) + min;
    }

    public void updateParticle(Particle globalBest) {
        for (int jobIndex = 0; jobIndex < LookupTable.numberOfJobs * LookupTable.numberOfMachines; jobIndex++) {
            // gets all particles
            Operation currentParticle = particle[jobIndex];
            Operation localParticle = localBest.particle[jobIndex];
            Operation globalParticle = globalBest.particle[jobIndex];

            // updates velocity and position
            currentParticle.velocity = Settings.inertiaWeight*currentParticle.velocity
                    + Settings.c1 * Math.random() * (localParticle.position - currentParticle.position)
                    + Settings.c2 * Math.random() * (globalParticle.position - currentParticle.position);

            currentParticle.position += currentParticle.velocity;

            if(currentParticle.velocity > Settings.vmax){
                currentParticle.velocity = Settings.vmax;
            }if(currentParticle.velocity < Settings.vmin){
                currentParticle.velocity = Settings.vmin;
            }if(currentParticle.position > Settings.xmax){
                currentParticle.position = Settings.xmax;
            }if(currentParticle.position < Settings.xmin){
                currentParticle.position = Settings.xmin;
            }
        }

        updateMakespan();

        if (this.makespan < localBest.makespan) {
            localBest = new Particle(this);
        }
    }

    public void updateMakespan() {

        /*Schedule s = new Schedule(particle);
        this.makespan = s.makespan;*/
        updateMakeSpan2();
    }


    // good speedup calculating makespan without creating the arraylist for the schedule
    public void updateMakeSpan2() {
        // making a copy of particle before sorting
        List<Operation> copy = new ArrayList<>(particle.length);
        for (int jobIdx = 0; jobIdx < particle.length; jobIdx++) {
            copy.add(particle[jobIdx]);
        }
        Collections.sort(copy);

        // lastOperation keeps track of last time a machine is executing each job
        int[] lastOperation = new int[LookupTable.numberOfJobs];
        // machineNumber keeps track of the current machineNumber doing the specified job
        int[] machineNumber = new int[LookupTable.numberOfJobs];

        for (int i = 0; i < LookupTable.numberOfJobs; i++) lastOperation[i] = -1;

        // machineDurations keeps track of the time used to execute all jobs for each machine
        int[] machineDurations = new int[LookupTable.numberOfMachines];

        for(Operation o: copy){
            int jobId = o.jobId;
            int machineId = LookupTable.jobOrder[jobId][machineNumber[jobId]++];

            // the end time of that job on another machine (possible start time for this job)
            int startTime = lastOperation[jobId];

            if(startTime > machineDurations[machineId]){
                machineDurations[machineId] = startTime + LookupTable.durations[o.jobId][machineId];
            }
            else{
                machineDurations[machineId] += LookupTable.durations[o.jobId][machineId];
            }
            if (machineDurations[machineId] > lastOperation[o.jobId]) {
                lastOperation[o.jobId] = machineDurations[machineId];
            }
        }
        // update makespan
        makespan = 0;
        for (int duration : machineDurations) {
            if (duration > makespan) {
                makespan = duration;
            }
        }
    }

    @Override
    public int compareTo(Particle o) {
        if (this.makespan > o.makespan) return 1;
        if (this.makespan < o.makespan) return -1;
        return 0;
    }
}
