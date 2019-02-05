import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Run {
    private int initialPopulation = 200;
    private double crossoverRate = 1;
    private double mutationRate = 1;
    private int maxGenerationNumber = 200;
    private double targetFitness = 0;
    private int elites = 180;
    private int participantNr = 6;

    private int generationNumber = 0;
    private double currentBestFitness = Double.MAX_VALUE;
    private List<Integer> individualIndexes;
    private List<Integer> customerIndexes = new ArrayList<>();
    private List<DNA> population = new ArrayList<>();


    private void runItBaby(String fileName) throws IOException {
        // Initializing population
        DataReader dr = new DataReader();
        dr.readFile("data/Data Files/" + fileName);
        this.targetFitness = dr.resultReader("data/Solution Files/" + fileName + ".res");
        DNA.vehicles = dr.vehicle_dict;
        DNA.depots = dr.depot_dict;
        DNA.customers = dr.customer_dict;
        for (int i = 0; i < this.initialPopulation; i++) {
            DNA dna = new DNA();
            this.population.add(dna);
        }
        for (int i = 0; i < dr.customer_dict.size(); i++){
            customerIndexes.add(i);
        }
        System.out.println(currentBestFitness);
        System.out.println(targetFitness);
        while (this.generationNumber <= this.maxGenerationNumber && currentBestFitness > targetFitness*1.05){
            this.individualIndexes = IntStream.rangeClosed(0, this.population.size()-1)
                    .boxed().collect(Collectors.toList());
            sortPopulationfFitness(this.population);

            System.out.println(this.population.get(0).getFitness());
            System.out.println("---------------------");

            //Logic for playing tournaments
            int tournamentRounds = this.population.size()-this.elites;
            List<DNA> newPopulation = new ArrayList<>();

            for(int j = 0; j < tournamentRounds; j++){
                Collections.shuffle(this.individualIndexes);
                List<DNA> participants = new ArrayList<>();
                for(int k = 0; k < this.participantNr; k++){
                    participants.add(this.population.get(this.individualIndexes.get(k)));
                }
                //returns two fittest in participants
                List<DNA> winners = this.playTurnament(participants);

                //Crossover the two winners to create two parents and add them to the new population
                List<DNA> children = this.crossover(winners);
                newPopulation.addAll(children);
            }
            for(int l = 0; l < this.elites; l++){
                newPopulation.add(population.get(l));
            }
            this.mutatePopulation(newPopulation);
            this.population = newPopulation;

            this.generationNumber += 1;
        }
        Visualizer vis = new Visualizer(dr.depot_dict, dr.customer_dict, dr.vehicle_dict,
                this.population.get(0).getDNAString(), dr.maxCoordinate, dr.minCoordinate);
    }

    private void sortPopulationfFitness(List<DNA> population) {
        //Sort list in ascending order
        population.sort(Comparator.comparingDouble(DNA::getFitness));
    }

    private List<DNA> playTurnament(List<DNA> participants){
        List<DNA> winners = new ArrayList<>();
        sortPopulationfFitness(participants);
        winners.add(participants.get(0));
        winners.add(participants.get(1));
        return winners;
    }

    private List<DNA> crossover(List<DNA> parents){
        return parents;
    }

    private void mutatePopulation(List<DNA> population){
        for(DNA individual: population){
            if( Double.valueOf(ThreadLocalRandom.current().nextInt(0, 100))/100 < this.mutationRate){
                mutateIndividual(individual);
            }
        }
    }

    private void mutateIndividual(DNA individual){
        Collections.shuffle(this.customerIndexes);
        int firstCustomerIdx = customerIndexes.get(0);
        int secondCustomerIdx = customerIndexes.get(1);
        int firstPosCol = 0;
        int firstPosRow = 0;
        int secondPosCol = 0;
        int secondPosRow = 0;
        List<List<Integer>> DNAString = individual.getDNAString();
        int customerCount = 0;
        for(int rowIdx = 0; rowIdx < DNAString.size(); rowIdx++){
            List<Integer> row = DNAString.get(rowIdx);
            for(int colIdx = 0; colIdx < row.size()-1; colIdx++){
                if(customerCount == firstCustomerIdx){
                    firstPosRow = rowIdx;
                    firstPosCol = colIdx;
                }
                else if(customerCount == secondCustomerIdx){
                    secondPosRow = rowIdx;
                    secondPosCol = colIdx;
                }
                customerCount++;
            }
        }
        int tempFirst = DNAString.get(firstPosRow).get(firstPosCol);
        DNAString.get(firstPosRow).set(firstPosCol, DNAString.get(secondPosRow).get(secondPosCol));
        DNAString.get(secondPosRow).set(secondPosCol, tempFirst);
        individual.updateFitness();
        //TODO: update end depot
    }

    public static void main(String[] args) throws IOException {
        Run run = new Run();
        run.runItBaby("p01");
    }





}
