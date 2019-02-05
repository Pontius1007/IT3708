import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Run {
    private int initialPopulation = 1000;
    private double crossoverRate = 1;
    private double mutationRate = 1;
    private int maxGenerationNumber = 1000;
    private int generationNumber = 0;
    private double targetFitness = 0;
    private double currentBestFitness = 0;

    private List<DNA> population = new ArrayList<>();


    private void runItBaby(String fileName) throws IOException {
        // Initializing population
        DataReader dr = new DataReader();
        dr.readFile("data/Data Files/" + fileName);
        this.targetFitness = dr.resultReader("data/Solution Files/" + fileName + ".res");
        for (int i = 0; i < this.maxGenerationNumber; i++) {
            DNA dna = new DNA(dr.vehicle_dict, dr.depot_dict, dr.customer_dict);
            this.population.add(dna);
        }

        while (this.generationNumber <= this.maxGenerationNumber || currentBestFitness*1.05 > targetFitness){
            break;
        }
    }

    public static void main(String[] args) throws IOException {
        Run run = new Run();
        run.runItBaby("p01");
    }





}
