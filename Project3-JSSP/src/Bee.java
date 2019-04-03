public class Bee extends Particle {

    public int repetitions;

    public Bee(Particle cloneParticle, double neighbourDistance) {
        super(cloneParticle);
        this.repetitions = 0;
        randomNeighbourhood(neighbourDistance);
    }

    public Bee(Particle cloneParticle) {
        super(cloneParticle);
        this.repetitions = 0;
    }

    public Bee() {
        super();
        this.repetitions = 0;
    }

    public void randomNeighbourhood(double neighbourDistance){
        for(int i = 0; i < this.particle.length; i++){
            this.particle[i].position += this.getRandomDoubleBetweenRange(-neighbourDistance/2, neighbourDistance/2);
        }
    }
}
