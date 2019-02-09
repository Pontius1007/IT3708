import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Run {
    private int initialPopulation = 1000;
    private double crossoverRate = 0;
    private double mutationRate = 1;
    private int maxGenerationNumber = 1000;
    private double targetFitness = 0;
    private int elites = 180;
    private int participantNr = 6;

    private int generationNumber = 0;
    private double currentBestFitness = Double.MAX_VALUE;
    private List<Integer> individualIndexes;
    private List<Integer> customerIndexes = new ArrayList<>();
    private List<DNA> population = new ArrayList<>();

    private int customerSize;
    private int vehicleSize;


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
        this.customerSize = dr.customer_dict.size();
        this.vehicleSize = dr.vehicle_dict.size();
        for (int i = 0; i < dr.customer_dict.size(); i++){
            customerIndexes.add(i);
        }
        while (this.generationNumber <= this.maxGenerationNumber && currentBestFitness > targetFitness*1.05){
            //Creates a list containing numbers from 0 to the size of the population.
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

                //Crossover the two winners to create one child and add them to the new population
                DNA child = this.crossover(winners);
                newPopulation.add(child);
            }
            this.mutatePopulation(newPopulation);
            for(int l = 0; l < this.elites; l++){
                newPopulation.add(population.get(l));
            }
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

    private DNA crossover(List<DNA> parents){
        //Trying out Route Based Crossover
        double parent1Share = 0.6;
        int minimumRoute = 2;
        int numberOfRoutes = parents.get(0).getDNAString().size();
        int parentMaxCrossIndex = (int)Math.round(parent1Share*numberOfRoutes) ;
        DNA child;
        if((double) ThreadLocalRandom.current().nextInt(0, 100) /100 < this.crossoverRate) {
            int crossIndex = ThreadLocalRandom.current().nextInt(minimumRoute, parentMaxCrossIndex);
            List<List<Integer>> NewDNAString = new ArrayList<>();
            //Add route from parent 1
            for (int route = 0; route < crossIndex; route++) {
                NewDNAString.add(parents.get(0).getDNAString().get(route));
            }
            //Add routes from parent 2
            for (int route = crossIndex; route < numberOfRoutes; route++) {
                NewDNAString.add(parents.get(1).getDNAString().get(route));
            }


            //TODO: ADD repair function to add missing customers and remove duplicates
            //TODO: Then create new DNA-object for the new child. Need new constructor
            child = new DNA(NewDNAString);
            repairRoute(child);

        }
        else{
            List<List<Integer>> DNAString = parents.get(0).getDNAString();
            List<List<Integer>> newDNAString = new ArrayList<>();
            for(List<Integer> route: DNAString){
                List<Integer> newRoute = new ArrayList<>();
                for(int customer: route){
                    newRoute.add(customer);
                }
                newDNAString.add(newRoute);
            }
            child = new DNA(newDNAString);
        }
        return child;
    }

    private void repairRoute(DNA child) {
        //Remove depots from DNA-string
        List<List<Integer>> DNAString = child.getDNAString();
        for (List<Integer> route : DNAString) {
            int depotIndex = route.size() - 1;
            if (route.size() >= 0) {
                route.remove(depotIndex);
            }
        }
        //Remove duplicate customers
        List<Integer> visitedCustomers = new ArrayList<>();
        for (List<Integer> route : DNAString) {
            //Looping backwards to avoid indexing issues when deleting element in list
            for (int customer = route.size()-1; customer >= 0; customer--) {
                if (visitedCustomers.contains(route.get(customer))) {
                    //Removes customer from list
                    //Kanskje dette vil gi skeive resulteter siden vi alltid fjerner kunden fra den siste ruten? Burde vi
                    //Gjøre denne smartere/random?
                    route.remove(customer);
                } else {
                    visitedCustomers.add(route.get(customer));
                }
            }

        }

        Collections.sort(visitedCustomers);
        List<Integer> missingCustomers = new ArrayList<>();
        for(int i = 0; i < this.customerSize; i++){
            if(!visitedCustomers.contains(i)){
                missingCustomers.add(i);
            }
        }

        List<Integer> possibleVehicles = IntStream.rangeClosed(0, this.vehicleSize-1)
                .boxed().collect(Collectors.toList());
        for(int missingCus: missingCustomers){
            child.addCustomer(missingCus, possibleVehicles);
        }
        child.addEndDepots();
    }

    private void mutatePopulation(List<DNA> population){
        for(DNA individual: population){
            if((double) ThreadLocalRandom.current().nextInt(0, 100) /100 < this.mutationRate){
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
        individual.updateEndDepots();
    }

    public static void main(String[] args) throws IOException {
        Run run = new Run();
        run.runItBaby("p01");
    }



}
