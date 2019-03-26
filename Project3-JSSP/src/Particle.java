public class Particle {

    public Operation[] particle;
    public double makespan;

    public Particle localBest;

    // copying particle constructor
    public Particle(Particle cloneParticle) {
        particle = new Operation[LookupTable.numberOfJobs*LookupTable.numberOfMachines];
        for(int job = 0; job < LookupTable.numberOfJobs*LookupTable.numberOfMachines; job ++){
            Operation cloneOperation = cloneParticle.particle[job];
            particle[job] = new Operation(cloneOperation.jobId, cloneOperation.machineId, cloneOperation.position, cloneOperation.velocity);
        }
        this.makespan = cloneParticle.makespan;
    }

    public Particle() {
        particle = new Operation[LookupTable.numberOfJobs*LookupTable.numberOfMachines];

        // adds all the operations to the particle with random position and velocity vectors
        for(int jobIdx = 0; jobIdx < LookupTable.numberOfJobs; jobIdx++){
            for(int machineIdx = 0; machineIdx < LookupTable.numberOfMachines; machineIdx++){
                particle[jobIdx*LookupTable.numberOfJobs+machineIdx] = new Operation(jobIdx, machineIdx,
                        getRandomDoubleBetweenRange(Settings.xmin, Settings.max),
                        getRandomDoubleBetweenRange(Settings.vmin, Settings.vmax));
            }
        }
        this.localBest = new Particle(this);
        updateMakespan();
    }


    public void updateParticle(Particle globalBest){
        for(int jobIndex = 0; jobIndex < LookupTable.numberOfJobs*LookupTable.numberOfMachines; jobIndex++){
            // gets all particles
            Operation currentParticle = particle[jobIndex];
            Operation localParticle = localBest.particle[jobIndex];
            Operation globalParticle = globalBest.particle[jobIndex];

            // updates velocity and position
            currentParticle.velocity = currentParticle.velocity
                    + Settings.c1*Math.random()*(localParticle.position - currentParticle.position)
                    + Settings.c2*Math.random()*(globalParticle.position - currentParticle.position);

            currentParticle.position += currentParticle.velocity;
        }

        updateMakespan();

        if(this.makespan < localBest.makespan){
            localBest = new Particle(this);
        }
    }

    public void updateMakespan(){
        Schedule s = new Schedule(particle);
        this.makespan = s.makespan;
    }

    public static double getRandomDoubleBetweenRange(double min, double max){
        return (Math.random()*((max-min)+1))+min;
    }
}
