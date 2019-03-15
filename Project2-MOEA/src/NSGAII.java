import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class NSGAII {
    //Real number is 2x
    private int populationNumber = 40;
    private int childPopulationNumber = 40;
    private double mutationRate = 0.05;
    private List<Chromosome> population = new ArrayList<>();
    private ArrayList<ArrayList<Chromosome>> rankedPopulation = new ArrayList<>();

    private int size;

    //TODO: Check for bugs. Has not been tested with solutions dominating each other
    private ArrayList<Chromosome> fastNondominatedSort(List<Chromosome> population) {
        Set<Chromosome> non_dominated_set = new HashSet<>();
        //Include first member in P'
        non_dominated_set.add(population.get(0));


        List<Chromosome> isDominated = new ArrayList<>();

        for (Chromosome individual : population) {
            if (isDominated.contains(individual)) {
                continue;
            }
            //Include p in P' temporarily
            non_dominated_set.add(individual);
            //Compare p with other members of P'
            for (Chromosome non_dominated : non_dominated_set) {
                if (isDominated.contains(individual)) {
                    continue;
                }
                if (non_dominated == individual) {
                    continue;
                }
                //If p dominates a member of P', delete it
                if (individual.getConnectivity() < non_dominated.getConnectivity() && individual.getDeviation() < non_dominated.getDeviation()) {
                    System.out.println("If p dominates a member of P', delete it");
                    isDominated.add(non_dominated);

                    //if p is dominated by other members of P', do not include p in P'
                } else if (individual.getConnectivity() > non_dominated.getConnectivity() && individual.getDeviation() > non_dominated.getDeviation()) {
                    System.out.println("if p is dominated by other members of P', do not include p in P'");
                    isDominated.add(individual);
                }
            }
        }

        population.removeAll(isDominated);

        return new ArrayList<>(non_dominated_set);
    }

    private void crowdingDistanceAssignment(List<Chromosome> pareto_front) {
        //initialize distance to 0
        for (Chromosome chromosome : pareto_front) chromosome.setCrowding_distance(0);
        if (pareto_front.get(0).isUseDeviation()) crowdingDistanceAssignmentPerObjective(pareto_front, 0);
        if (pareto_front.get(0).isUseConnectivity()) crowdingDistanceAssignmentPerObjective(pareto_front, 1);
    }

    private void crowdingDistanceAssignmentPerObjective(List<Chromosome> pareto_front, int sortingObjectiveIndex) {
        if (sortingObjectiveIndex == 0) pareto_front.sort(Chromosome.deviationComparator());
        if (sortingObjectiveIndex == 1) pareto_front.sort(Chromosome.connectivityComparator());
        //Set boundries so they are always detected
        pareto_front.get(0).setCrowding_distance(Double.POSITIVE_INFINITY);
        pareto_front.get(pareto_front.size() - 1).setCrowding_distance(Double.POSITIVE_INFINITY);

        for (int i = 1; i < pareto_front.size() - 1; i++) {
            if (sortingObjectiveIndex == 0) {
                pareto_front.get(i).setCrowding_distance(pareto_front.get(i).getCrowding_distance() + (pareto_front.get(i + 1).getDeviation() - pareto_front.get(i - 1).getDeviation()));
            } else {
                pareto_front.get(i).setCrowding_distance(pareto_front.get(i).getCrowding_distance() + (pareto_front.get(i + 1).getConnectivity() - pareto_front.get(i - 1).getConnectivity()));
            }
        }

    }

    private void initializePopulation(ImageMat loadImg) {
        List<Chromosome> populationInProgress = Collections.synchronizedList(new ArrayList<>(this.populationNumber * 2));

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < this.populationNumber * 2; i++) {
            executorService.execute(() -> {
                Chromosome temp = new Chromosome(loadImg, ThreadLocalRandom.current().nextInt(20, 100));
                //TODO: Legg til kall her for å legge til segmenter mindre enn k kanskje?
                populationInProgress.add(temp);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        this.population.addAll(populationInProgress);
    }

    private void rankPopulation() {
        rankedPopulation.clear();
        ArrayList<Chromosome> rankList;
        int rank = 1;
        while (this.population.size() > 0) {
            rankList = fastNondominatedSort(this.population);
            for (Chromosome x : rankList) {
                x.setRank(rank);
            }
            rankedPopulation.add(rankList);
            population.removeAll(rankList);
            rank++;
        }
        for (List<Chromosome> list : rankedPopulation) {
            population.addAll(list);
        }
    }

    private void runMainLoop(String imageFile) {
        ImageMat loadImg = new ImageMat(imageFile);
        initializePopulation(loadImg);
        rankPopulation();
        //Creates new population of size N based on initial population in generation 0
        //Not following the psudo-code correctly here, as I doubt it really matters. Easier to do it this day
        //createNewPopulationBasedOnRank();
        //Following the psudo-code:
        this.population = createChildren(loadImg, true);

        int generation = 1;

        while (true) {
            //Print status
            printStatus(generation);
            //Create offsprings
            List<Chromosome> children = createChildren(loadImg, false);
            population.addAll(children);
            rankPopulation();
            createNewPopulationBasedOnRank();
            //Should be use selection, crossover and mutation to create a new population of size N here?
            generation++;

        }
    }

    private List<Chromosome> createChildren(ImageMat loadImg, boolean generationZero) {
        int multiplier = (generationZero) ? 2 : 1;
        List<Chromosome> children = Collections.synchronizedList(new ArrayList<>(this.childPopulationNumber));

        final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (int i = 0; i < this.childPopulationNumber * multiplier; i++) {
            executorService.execute(() -> {
                Chromosome father = selectParent();
                Chromosome mother = selectParent();
                Chromosome child = new Chromosome(loadImg, father, mother, mutationRate);
                children.add(child);
            });
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        return children;
    }

    private void createNewPopulationBasedOnRank() {
        population.clear();
        for (List<Chromosome> pareto_front : rankedPopulation) {
            crowdingDistanceAssignment(pareto_front);
            //Add the pareto-front to the population if space
            if (pareto_front.size() <= this.populationNumber - population.size()) {
                population.addAll(pareto_front);
            } else {
                ArrayList<Chromosome> pareto_front_copy = new ArrayList<>(pareto_front);
                pareto_front_copy.sort(Chromosome.nonDominatedCrowdingComparator());
                while (population.size() < this.populationNumber) {
                    population.add(pareto_front_copy.remove(0));
                }
            }
        }

    }

    private Chromosome selectParent() {
        //Binary tournament selection
        int indx1;
        int indx2;
        indx1 = new SplittableRandom().nextInt(0, population.size());
        indx2 = new SplittableRandom().nextInt(0, population.size());
        while (indx1 == indx2) {
            indx2 = new SplittableRandom().nextInt(0, population.size());
        }
        Chromosome p1 = population.get(indx1);
        Chromosome p2 = population.get(indx2);
        if (Chromosome.nonDominatedCrowdingComparator().compare(p1, p2) < 0) return p1;
        return p2;
    }

    private void printStatus(int generation) {
        System.out.println("This is generation: " + generation);
        System.out.println("Size of population " + this.population.size());
    }

    public static void main(String[] args) {
        NSGAII run = new NSGAII();
        run.runMainLoop("86016");
    }
}
