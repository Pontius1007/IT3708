import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DNA {

    private List<List<Integer>> DNAString;
    private List<Integer> vehicleWeights;
    private List<Integer> vehicleDistance;
    private double totalDistance;
    private double fitness;
    private double punnishment;
    private double punnishmentRate = 100;

    public static List<List<Double>> neightbourMatrix;

    public DNA(Map<Integer, Vehicle> vehicles, Map<Integer, Depot> depots, Map<Integer, Customer> customers) {
        DNAString = new ArrayList<>();
        vehicleWeights = new ArrayList<>();
        vehicleDistance = new ArrayList<>();
        initializeDnaRandomly(vehicles, depots, customers);
        /* System.out.println("Distance: ");
        System.out.println(this.totalDistance);
        System.out.println("Fitness: ");
        System.out.println(this.fitness); */
    }

    public void initializeDnaRandomly(Map<Integer, Vehicle> vehicles, Map<Integer, Depot> depots, Map<Integer, Customer> customers){
        List<Integer> possibleVehicles = new ArrayList<>();
        for (int x = 0; x < vehicles.size(); x++){
            DNAString.add(new ArrayList<>());
            vehicleWeights.add(vehicles.get(x).getMaxLoad());
            possibleVehicles.add(x);
        }
        //add all customers randomly
        for (Customer customer: customers.values()) {
            addCustomer(customer, customers, vehicles, possibleVehicles);
        }

        //add closest ending depot for each vehicle
        for(int routeID = 0; routeID < DNAString.size(); routeID++){
            List<Integer> route = this.DNAString.get(routeID);
            int closestDepotId = 0;
            double closestDepotDistance = Double.MAX_VALUE;
            for(int i = 0; i < depots.size(); i++){
                if(route.size() == 0){
                    closestDepotId = vehicles.get(routeID).getDepotID();
                    closestDepotDistance = 0;
                }
                else{
                    double currentDistance = neightbourMatrix.get(route.get(route.size()-1)).get(customers.size()+i);
                    if(currentDistance < closestDepotDistance){
                        closestDepotId = i;
                        closestDepotDistance = currentDistance;
                    }
                }
            }
            route.add(closestDepotId);
            this.punnishment += Math.max(0, calculateRouteLength(route, vehicles.get(routeID).getDepotID(), customers.size())-vehicles.get(routeID).getMaxDuration());
        }
        this.punnishment *= this.punnishmentRate;
        this.totalDistance = this.calculateFitness(vehicles, customers.size());
        this.fitness = this.totalDistance + this.punnishment;
    }


    private void addCustomer(Customer customer, Map<Integer, Customer> customers, Map<Integer, Vehicle> vehicles, List<Integer> possibleVehicles){
        if(possibleVehicles.size() == 0){
            throw new IllegalStateException("No more room in any vehicles (No room for weight in any vehicle)");
        }
        int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, possibleVehicles.size());
        int randomVehicle = possibleVehicles.get(randomVehicleIdx);
        double routeWeight = this.testRouteWeight(customers, this.DNAString.get(randomVehicle), customer);
        if (customer.getWeight() < vehicleWeights.get(randomVehicle)) {
            this.DNAString.get(randomVehicle).add(customer.getCustomerID());
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
        return distance;
    }

    private double testRouteWeight(Map<Integer, Customer> customers, List<Integer> route, Customer newCustomer){
        double totalWeight = 0;
        for(int i = 0; i < route.size(); i++){
            Customer currentCustomer = customers.get(i);
            totalWeight += currentCustomer.getWeight();
        }
        totalWeight += newCustomer.getWeight();
        return totalWeight;
    }

    public List<List<Integer>> getDNAString() {
        return DNAString;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getFitness() {
        return fitness;
    }
}