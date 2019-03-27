import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Particle {

    public Operation[] particle;
    public double makespan;

    public Particle localBest;

    // copying particle constructor
    public Particle(Particle cloneParticle) {
        particle = new Operation[LookupTable.numberOfJobs * LookupTable.numberOfMachines];
        for (int job = 0; job < LookupTable.numberOfJobs * LookupTable.numberOfMachines; job++) {
            Operation cloneOperation = cloneParticle.particle[job];
            particle[job] = new Operation(cloneOperation.jobId, cloneOperation.machineId, cloneOperation.position, cloneOperation.velocity);
        }
        this.makespan = cloneParticle.makespan;
    }

    public Particle() {
        particle = new Operation[LookupTable.numberOfJobs * LookupTable.numberOfMachines];

        // adds all the operations to the particle with random position and velocity vectors
        for (int jobIdx = 0; jobIdx < LookupTable.numberOfJobs; jobIdx++) {
            for (int machineIdx = 0; machineIdx < LookupTable.numberOfMachines; machineIdx++) {
                particle[jobIdx * LookupTable.numberOfJobs + machineIdx] = new Operation(jobIdx, machineIdx,
                        getRandomDoubleBetweenRange(Settings.xmin, Settings.xmax),
                        getRandomDoubleBetweenRange(Settings.vmin, Settings.vmax));
            }
        }
        this.localBest = new Particle(this);
        updateMakespan();
    }


    public void updateParticle(Particle globalBest) {
        for (int jobIndex = 0; jobIndex < LookupTable.numberOfJobs * LookupTable.numberOfMachines; jobIndex++) {
            // gets all particles
            Operation currentParticle = particle[jobIndex];
            Operation localParticle = localBest.particle[jobIndex];
            Operation globalParticle = globalBest.particle[jobIndex];

            // updates velocity and position
            currentParticle.velocity = currentParticle.velocity
                    + Settings.c1 * Math.random() * (localParticle.position - currentParticle.position)
                    + Settings.c2 * Math.random() * (globalParticle.position - currentParticle.position);

            currentParticle.position += currentParticle.velocity;
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


    public void updateMakeSpan2() {
        // making a copy of particle before sorting
        List<Operation> copy = new ArrayList<>(particle.length);
        for (int jobIdx = 0; jobIdx < particle.length; jobIdx++) {
            copy.add(particle[jobIdx]);
        }
        Collections.sort(copy);

        // lastOperation keeps track of last machine executing each job
        int[] lastOperation = new int[LookupTable.numberOfJobs];
        for (int i = 0; i < LookupTable.numberOfJobs; i++) lastOperation[i] = -1;

        // lastJob keeps track of the last timeunit that job was executed
        int[] lastJob = new int[LookupTable.numberOfJobs];

        int[] machineDurations = new int[LookupTable.numberOfMachines];

        while (copy.size() > 0) {
            // finds the best legal operation to add
            Operation bestOp = copy.get(0);
            for (Operation o : copy) {
                int requiredPrevious = LookupTable.previousMachine.get(o.jobId).get(o.machineId);
                if (requiredPrevious == -1 || requiredPrevious == lastOperation[o.jobId]) {
                    bestOp = o;
                    break;
                }
            }
            int lastUsed = lastJob[bestOp.jobId];
            // if machine needs to wait for job to finnish on another machine
            if (lastUsed > machineDurations[bestOp.machineId]) {
                machineDurations[bestOp.machineId] = lastUsed + LookupTable.durations[bestOp.jobId][bestOp.machineId];
            } else {
                machineDurations[bestOp.machineId] += LookupTable.durations[bestOp.jobId][bestOp.machineId];
            }
            if (machineDurations[bestOp.machineId] > lastJob[bestOp.jobId]) {
                lastJob[bestOp.jobId] = machineDurations[bestOp.machineId];
            }
            lastOperation[bestOp.jobId] = bestOp.machineId;
            copy.remove(bestOp);
        }
        makespan = 0;
        for (int duration : machineDurations) {
            if (duration > makespan) {
                makespan = duration;
            }
        }
    }

    public static double getRandomDoubleBetweenRange(double min, double max) {
        return (Math.random() * ((max - min) + 1)) + min;
    }
}
