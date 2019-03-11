import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NSGAII {
    private int populationNumber = 5;
    private List<Chromosome> population;

    //In this approach, every solution from the population is checked with a partially filled population for domination.
    private Set<Chromosome> fast_nondominated_sort(List<Chromosome> population) {
        Set<Chromosome> non_dominated_set = new HashSet<>();
        //Include first member in P'
        non_dominated_set.add(population.get(0));

        for (Chromosome individual : population) {
            //Include p in P' temporarily
            non_dominated_set.add(individual);
            //Compare p with other members of P'
            for (Chromosome non_dominated : non_dominated_set) {
                if (non_dominated == individual) {
                    continue;
                }
                //If p dominates a member of P', delete it
                if (individual.getConnectivity() < non_dominated.getConnectivity() && individual.getDeviation() < non_dominated.getDeviation()) {
                    System.out.println("If p dominates a member of P', delete it");
                    non_dominated_set.remove(non_dominated);
                    //if p is dominated by other members of P', do not include p in P'
                } else if (individual.getConnectivity() > non_dominated.getConnectivity() && individual.getDeviation() > non_dominated.getDeviation()) {
                    System.out.println("if p is dominated by other members of P', do not include p in P'");
                    non_dominated_set.remove(individual);
                }
            }
        }
        return non_dominated_set;
    }

    private void runMainLoop(String imageFile) {
        ImageMat loadImg = new ImageMat(imageFile);
        this.population = new ArrayList<>();
        for (int i = 0; i < populationNumber; i++) {
            this.population.add(new Chromosome(loadImg, i + 2));
        }
        Set<Chromosome> test = fast_nondominated_sort(this.population);
        System.out.println(test.size());
        System.out.println(population.size());
    }


    public static void main(String[] args) {
        NSGAII run = new NSGAII();
        run.runMainLoop("86016");

    }


}
