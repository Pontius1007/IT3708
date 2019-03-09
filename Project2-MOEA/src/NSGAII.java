import java.util.ArrayList;
import java.util.List;

public class NSGAII {
    private int populationNumber = 10;
    private List<Chromosome> population;

    //In this approach, every solution from the population is checked with a partially filled population for domination.
    private void fast_nondominated_sort(List<Chromosome> population) {
        List<Chromosome> non_dominated_set = new ArrayList<>();
        non_dominated_set.add(population.get(0));

        for (Chromosome individual : population) {
            
        }
    }

    private void runMainLoop(String imageFile) {
        ImageMat loadImg = new ImageMat(imageFile);
        this.population = new ArrayList<>();
        for (int i = 0; i < populationNumber; i++) {
            this.population.add(new Chromosome(loadImg, 10));
        }

        fast_nondominated_sort(this.population);
    }


    public static void main(String[] args) {
        NSGAII run = new NSGAII();
        run.runMainLoop("86016");

    }





}
