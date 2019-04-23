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
        for (int i = 0; i < this.particle.length; i++) {
            this.particle[i].position += (new SplittableRandom().nextInt(0, 100)-50)/50;
        }
        this.updateMakespan();
    }
}
