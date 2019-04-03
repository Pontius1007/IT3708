public class Bee extends Particle {

    public int repetitions;

    public Bee(Bee cloneParticle, Bee randNeighbour) {
        super(cloneParticle);
        this.repetitions = 0;
        randomNeighbourhood(randNeighbour);
    }

    public Bee(Bee cloneParticle) {
        super(cloneParticle);
        this.repetitions = 0;
    }

    public Bee() {
        super();
        this.repetitions = 0;
    }

    public void randomNeighbourhood(Bee randNeighbour){
        for(int i = 0; i < this.particle.length; i++){
            this.particle[i].position += this.getRandomDoubleBetweenRange(-Settings.neighbourhoodSize, Settings.neighbourhoodSize)
            * (this.particle[i].position-randNeighbour.particle[i].position);
        }
    }
}
