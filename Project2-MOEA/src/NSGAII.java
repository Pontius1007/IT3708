import jdk.nashorn.internal.runtime.regexp.joni.encoding.CharacterType;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class NSGAII {
    private int populationNumber = 5;
    private ArrayList<Chromosome> population = new ArrayList<>();
    private ArrayList<ArrayList<Chromosome>> rankedPopulation = new ArrayList<>();

    //TODO: Check for bugs. Has not been tested with solutions dominating each other
    private ArrayList<Chromosome> fastNondominatedSort(ArrayList<Chromosome> population) {
        Set<Chromosome> non_dominated_set = new HashSet<>();
        //Include first member in P'
        non_dominated_set.add(population.get(0));


        List<Chromosome> isDominated = new ArrayList<>();

        for (Chromosome individual : population) {
            if(isDominated.contains(individual)){
                continue;
            }
            //Include p in P' temporarily
            non_dominated_set.add(individual);
            //Compare p with other members of P'
            for (Chromosome non_dominated : non_dominated_set) {
                if(isDominated.contains(individual)){
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
        for (int i = 0; i < this.populationNumber; i++) {
            Chromosome temp = new Chromosome(loadImg, ThreadLocalRandom.current().nextInt(20, 100));
            //TODO: Legg til kall her for å legge til segmenter mindre enn k kanskje?
            this.population.add(temp);
        }

    }

    private void rankPopulation() {
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
    }

    private void runMainLoop(String imageFile) {
        ImageMat loadImg = new ImageMat(imageFile);
        initializePopulation(loadImg);
        rankPopulation();
        System.out.println("rankedPopulation size" + rankedPopulation.size());
        //TODO: binary tournament selection
        //Todo: recombination/crossover
        //TODO: mutation
        //Use this to create a child population of size N

        //After the first iteration, the main loop comes here as it differs from the first iteration. Check the document.
    }

    private void tournament(Chromosome[] participants){
    }

    public static void main(String[] args) {
        NSGAII run = new NSGAII();
        run.runMainLoop("86016");
    }
}
