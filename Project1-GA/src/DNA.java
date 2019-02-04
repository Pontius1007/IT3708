import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DNA {

    private List<List<Integer>> DNAString;
    private List<Integer> vehicleWeights;
    private List<Integer> vehicleDistance;
    private double fitness;

    public static List<List<Double>> neightbourMatrix;

    public DNA(Map<Integer, Vehicle> vehicles, Map<Integer, Depot> depot, Map<Integer, Customer> customers) {
        DNAString = new ArrayList<>();
        vehicleWeights = new ArrayList<>();
        vehicleDistance = new ArrayList<>();
        List<Integer> possibleVehicles = new ArrayList<>();
        for (int x = 0; x < vehicles.size(); x++){
            DNAString.add(new ArrayList<Integer>());
            vehicleWeights.add(vehicles.get(x).getMaxLoad());
            vehicleDistance.add(vehicles.get(x).getMaxDuration());
            int randomDepot = ThreadLocalRandom.current().nextInt(0, depot.size());
            DNAString.get(x).add(randomDepot);
            possibleVehicles.add(x);
            //System.out.println(DNAString.get(x));
        }
        for (Customer customer: customers.values()) {
            addCustomer(customer, customers, vehicles, possibleVehicles);
        }
        this.fitness = this.calculateFitness(vehicles, customers.size());
        System.out.println(this.fitness);
    }

    private void addCustomer(Customer customer, Map<Integer, Customer> customers, Map<Integer, Vehicle> vehicles, List<Integer> possibleVehicles){
        //TODO: serparate exeption from weight and lenght capacity
        if(possibleVehicles.size() == 0){
            throw new IllegalStateException("No more room in any vehicles or exceeded length");
        }
        int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, possibleVehicles.size());
        int randomVehicle = possibleVehicles.get(randomVehicleIdx);
        double routeLength =  testRouteLength(this.DNAString.get(randomVehicle), vehicles.get(randomVehicle).getDepotID(),
                customers.size(), customer.getCustomerID());
        if (customer.getWeight() < vehicleWeights.get(randomVehicle) && routeLength <= vehicleDistance.get(randomVehicle)) {
            this.DNAString.get(randomVehicle).add(0, customer.getCustomerID());
        }
        else{
            possibleVehicles.remove(randomVehicleIdx);
            addCustomer(customer, customers, vehicles, possibleVehicles);
        }
    }

    public void printMatrix(List<List<Integer>> mat){
        for(List<Integer> route: mat){
            String resultString = "";
            for(int value: route){
                resultString += (" "+Integer.toString(value));
            }
            System.out.println(resultString);
        }
    }


    private double calculateFitness(Map<Integer, Vehicle> vehicles, int customerSize){
        double fitness = 0;
        for(int i = 0; i < this.DNAString.size(); i++){
            List<Integer> route = this.DNAString.get(i);
            fitness += calculateRouteLength(route, vehicles.get(i).getDepotID(), customerSize);
        }
        return fitness;
    }

    private double calculateRouteLength(List<Integer> route, int startDepotId, int customerSize) {
        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previous = customerSize-1 + startDepotId;
        //Adds distance between all customers
        for(int i = 0; i < route.size() - 1; i++) {
            int current = route.get(i);
            distance += neightbourMatrix.get(previous).get(current);
            previous = current;
        }
        //Adds distance from last customer to end depot
        int current = customerSize-1 + route.get(route.size()-1);
            distance += neightbourMatrix.get(previous).get(current);
        return distance;
    }

    private double testRouteLength(List<Integer> route, int startDepotId, int customerSize, int newCustomer) {

        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previous = customerSize-1 + startDepotId;
        //Calculates the distance from start depot to the new customer
        int current = newCustomer;
        distance += neightbourMatrix.get(previous).get(current);
        previous = current;
        //Calutes distances between all alread added customers
        for(int i = 0; i < route.size() - 1; i++) {
            current = route.get(i);
            distance += neightbourMatrix.get(previous).get(current);
            previous = current;
        }
        //Calculates distance from last customer to the end depot
        current = customerSize-1 + route.get(route.size()-1);
        distance += neightbourMatrix.get(previous).get(current);
        return distance;
    }

    public List<List<Integer>> getDNAString() {
        return DNAString;
    }
}