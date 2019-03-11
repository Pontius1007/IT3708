import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NSGAII {
    private int populationNumber = 5;
    private List<Chromosome> population;

    //TODO: Check for bugs. Has not been tested with solutions dominating each other
    private List<Chromosome> fast_nondominated_sort(List<Chromosome> population) {
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
        return new ArrayList<>(non_dominated_set);
    }

    private void crowding_distance_assignment(List<Chromosome> pareto_front) {
        //initialize distance to 0
        for (Chromosome chromosome : pareto_front) chromosome.setCrowding_distance(0);
        if (pareto_front.get(0).isUseDeviation()) crowding_distance_assignment_per_objective(pareto_front, 0);
        if (pareto_front.get(0).isUseConnectivity()) crowding_distance_assignment_per_objective(pareto_front, 1);
    }

    private void crowding_distance_assignment_per_objective(List<Chromosome> pareto_front, int sortingObjectiveIndex) {
        if (sortingObjectiveIndex == 0) pareto_front.sort(Chromosome.deviationComparator());
        if (sortingObjectiveIndex == 1) pareto_front.sort(Chromosome.connectivityComparator());
        //Set boundries so they are always detected
        pareto_front.get(0).setCrowding_distance(Double.POSITIVE_INFINITY);
        pareto_front.get(pareto_front.size()-1).setCrowding_distance(Double.POSITIVE_INFINITY);

        for (int i = 1; i < pareto_front.size()-1; i++) {
            if (sortingObjectiveIndex == 0) {
                pareto_front.get(i).setCrowding_distance(pareto_front.get(i).getCrowding_distance() + (pareto_front.get(i+1).getDeviation() - pareto_front.get(i-1).getDeviation()));
            }
            else {
                pareto_front.get(i).setCrowding_distance(pareto_front.get(i).getCrowding_distance() + (pareto_front.get(i+1).getConnectivity() - pareto_front.get(i-1).getConnectivity()));
            }
        }

    }

    private void runMainLoop(String imageFile) {
        ImageMat loadImg = new ImageMat(imageFile);
        this.population = new ArrayList<>();
        for (int i = 0; i < populationNumber; i++) {
            this.population.add(new Chromosome(loadImg, i + 2));
        }
        List<Chromosome> test = fast_nondominated_sort(this.population);
        System.out.println(test.size());
        System.out.println(population.size());
        crowding_distance_assignment(test);
    }


    public static void main(String[] args) {
        NSGAII run = new NSGAII();
        run.runMainLoop("86016");

    }


}
