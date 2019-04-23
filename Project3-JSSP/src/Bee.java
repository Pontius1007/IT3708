import java.util.SplittableRandom;

public class Bee extends Particle {

    public int repetitions;

    public Bee(Bee cloneParticle, boolean isScout) {
        super(cloneParticle);
        this.repetitions = 0;
        randomNeighbourhood();
    }

    public Bee(Bee cloneParticle) {
        super(cloneParticle);
        this.repetitions = 0;
    }

    public Bee() {
        super();
        this.repetitions = 0;
    }

    public void randomNeighbourhood(){
        int pos1 = new SplittableRandom().nextInt(0, this.particle.length);
        int pos2 = new SplittableRandom().nextInt(0, this.particle.length);
        double cache1 = this.particle[pos1].position;
        this.particle[pos1].position = this.particle[pos2].position;
        this.particle[pos2].position = cache1;

        this.updateMakespan();
    }
}
