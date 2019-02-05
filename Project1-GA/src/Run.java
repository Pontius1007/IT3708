import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Run {
    private int initialPopulation = 1000;
    private double crossoverRate = 1;
    private double mutationRate = 1;
    private int maxGenerationNumber = 1;
    private int generationNumber = 0;
    private double targetFitness = 0;
    private double currentBestFitness = 0;
    private List<Integer> individualIndexes;



    private List<DNA> population = new ArrayList<>();


    private void runItBaby(String fileName) throws IOException {
        // Initializing population
        DataReader dr = new DataReader();
        dr.readFile("data/Data Files/" + fileName);
        this.targetFitness = dr.resultReader("data/Solution Files/" + fileName + ".res");
        for (int i = 0; i < this.initialPopulation; i++) {
            DNA dna = new DNA(dr.vehicle_dict, dr.depot_dict, dr.customer_dict);
            this.population.add(dna);
        }

        while (this.generationNumber <= this.maxGenerationNumber && currentBestFitness < targetFitness*1.05){
            this.individualIndexes = IntStream.rangeClosed(0, this.population.size()-1)
                    .boxed().collect(Collectors.toList());
            sortPopulationfFitness(this.population);
            this.currentBestFitness = this.population.get(0).getFitness();

            //Logic for playing tournaments
            int tournamentRounds = this.population.size()/2;
            int participantNr = 10;
            List<DNA> newPopulation = new ArrayList<>();

            for(int j = 0; j < tournamentRounds; j++){
                Collections.shuffle(this.individualIndexes);
                List<DNA> participants = new ArrayList<>();
                for(int k = 0; k < participantNr; k++){
                    participants.add(this.population.get(this.individualIndexes.get(k)));
                }
                //returns two fittest in participants
                List<DNA> winners = this.playTurnament(participants);

                //Crossover the two winners to create two parents and add them to the new population
                List<DNA> children = this.crossover(winners);
                newPopulation.addAll(children);
            }
            for(int l = 0; l < this.population.size()/2; l++){
                newPopulation.add(population.get(l));
            }
            this.mutate(newPopulation);
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

    private void mutate(List<DNA> population){
        for(DNA individual: population){

        }
    }

    public static void main(String[] args) throws IOException {
        Run run = new Run();
        run.runItBaby("p01");
    }





}
