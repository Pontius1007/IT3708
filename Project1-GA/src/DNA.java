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
    private static Map<Integer, Vehicle> vehicles;
    private static Map<Integer, Depot> depots;
    private static Map<Integer, Customer> customers;

    public DNA() {
        this.DNAString = new ArrayList<>();
        this.vehicleWeights = new ArrayList<>();
        this.vehicleDistance = new ArrayList<>();
        initializeDnaRandomly();
        /* System.out.println("Distance: ");
        System.out.println(this.totalDistance);
        System.out.println("Fitness: ");
        System.out.println(this.fitness); */
    }

    public void initializeDnaRandomly(){
        List<Integer> possibleVehicles = new ArrayList<>();
        for (int x = 0; x < this.vehicles.size(); x++){
            this.DNAString.add(new ArrayList<>());
            this.vehicleWeights.add(this.vehicles.get(x).getMaxLoad());
            possibleVehicles.add(x);
        }
        //add all customers randomly
        for (Customer customer: this.customers.values()) {
            addCustomer(customer, possibleVehicles);
        }

        //add closest ending depot for each vehicle
        for(int routeID = 0; routeID < this.DNAString.size(); routeID++){
            List<Integer> route = this.DNAString.get(routeID);
            int closestDepotId = 0;
            double closestDepotDistance = Double.MAX_VALUE;
            for(int i = 0; i < this.depots.size(); i++){
                if(route.size() == 0){
                    closestDepotId = this.vehicles.get(routeID).getDepotID();
                    closestDepotDistance = 0;
                }
                else{
                    double currentDistance = neightbourMatrix.get(route.get(route.size()-1)).get(this.customers.size()+i);
                    if(currentDistance < closestDepotDistance){
                        closestDepotId = i;
                        closestDepotDistance = currentDistance;
                    }
                }
            }
            route.add(closestDepotId);
            this.punnishment += Math.max(0, calculateRouteLength(route, this.vehicles.get(routeID).getDepotID(), this.customers.size())-this.vehicles.get(routeID).getMaxDuration());
        }
        this.punnishment *= this.punnishmentRate;
        this.totalDistance = this.calculateFitness(this.vehicles, this.customers.size());
        this.fitness = this.totalDistance + this.punnishment;
    }

    public void updateFitness(){
        for(int routeID = 0; routeID < DNAString.size(); routeID++){
            List<Integer> route = this.DNAString.get(routeID);
            this.punnishment += Math.max(0, calculateRouteLength(route, this.vehicles.get(routeID).getDepotID(),
                    this.customers.size())-this.vehicles.get(routeID).getMaxDuration());
        }
        this.punnishment *= this.punnishmentRate;
        this.totalDistance = this.calculateFitness(vehicles, customers.size());
        this.fitness = this.totalDistance + this.punnishment;
    }


    private void addCustomer(Customer customer, List<Integer> possibleVehicles){
        if(possibleVehicles.size() == 0){
            throw new IllegalStateException("No more room in any vehicles (No room for weight in any vehicle)");
        }
        int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, possibleVehicles.size());
        int randomVehicle = possibleVehicles.get(randomVehicleIdx);
        double routeWeight = this.testRouteWeight(this.DNAString.get(randomVehicle), customer);
        if (customer.getWeight() < vehicleWeights.get(randomVehicle)) {
            this.DNAString.get(randomVehicle).add(customer.getCustomerID());
        }
        else{
            possibleVehicles.remove(randomVehicleIdx);
            addCustomer(customer, possibleVehicles);
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


    private double calculateFitness(){
        double fitness = 0;
        for(int i = 0; i < this.DNAString.size(); i++){
            List<Integer> route = this.DNAString.get(i);
            fitness += calculateRouteLength(route, this.vehicles.get(i).getDepotID());
        }
        return fitness;
    }

    private double calculateRouteLength(List<Integer> route, int startDepotId) {
        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previous = this.customers.size()-1 + startDepotId;
        //Adds distance between all customers
        for(int i = 0; i < route.size() - 1; i++) {
            int current = route.get(i);
            distance += neightbourMatrix.get(previous).get(current);
            previous = current;
        }
        //Adds distance from last customer to end depot
        int current = this.customers.size()-1 + route.get(route.size()-1);
            distance += neightbourMatrix.get(previous).get(current);
        return distance;
    }

    private double testRouteLength(List<Integer> route, int startDepotId, int newCustomer) {

        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previous = this.customers.size()-1 + startDepotId;
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

    private double testRouteWeight(List<Integer> route, Customer newCustomer){
        double totalWeight = 0;
        for(int i = 0; i < route.size(); i++){
            Customer currentCustomer = this.customers.get(i);
            totalWeight += currentCustomer.getWeight();
        }
        totalWeight += newCustomer.getWeight();
        return totalWeight;
    }

    public List<List<Integer>> getDNAString() {
        return this.DNAString;
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public double getFitness() {
        return this.fitness;
    }
}