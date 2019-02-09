import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DNA {

    private List<List<Integer>> DNAString;
    private List<Integer> vehicleWeights;
    private double totalDistance;
    private double fitness;
    private double punnishment;
    private double punnishmentRate = 100;

    public static List<List<Double>> neightbourMatrix;
    public static Map<Integer, Vehicle> vehicles;
    public static Map<Integer, Depot> depots;
    public static Map<Integer, Customer> customers;

    public DNA() {
        this.DNAString = new ArrayList<>();
        this.vehicleWeights = new ArrayList<>();
        initializeDnaRandomly();
    }


    public DNA(List<List<Integer>> DNAString){
        this.DNAString = DNAString;
        this.vehicleWeights = new ArrayList<>();
        for (int x = 0; x < this.vehicles.size(); x++) {
            this.vehicleWeights.add(this.vehicles.get(x).getMaxLoad());
        }
    }


    public void initializeDnaRandomly() {
        List<Integer> possibleVehicles = new ArrayList<>();
        for (int x = 0; x < this.vehicles.size(); x++) {
            this.DNAString.add(new ArrayList<>());
            this.vehicleWeights.add(this.vehicles.get(x).getMaxLoad());
            possibleVehicles.add(x);
        }
        //add all customers randomly to a vehicle
        for (int i = 0; i < customers.size(); i++) {
            addCustomer(i, possibleVehicles);
        }

        //add closest ending depot for each vehicle
        this.addEndDepots();
    }


    public void updateEndDepots() {
        //remove old end depots
        for (List<Integer> route : this.getDNAString()) {
            route.remove(route.size() - 1);
        }
        //add new end depots
        this.addEndDepots();
    }


    public void addEndDepots() {
        for (int routeID = 0; routeID < this.DNAString.size(); routeID++) {
            List<Integer> route = this.DNAString.get(routeID);
            int closestDepotId = 0;
            double closestDepotDistance = Double.MAX_VALUE;
            for (int i = 0; i < this.depots.size(); i++) {
                //if there are no customers in the route, add startdepot as enddepot
                if (route.size() == 0) {
                    closestDepotId = this.vehicles.get(routeID).getDepotID();
                    closestDepotDistance = 0;

                    /*double currentDistance = neightbourMatrix.get(this.customers.size() + this.vehicles.get(routeID).getDepotID()).get(this.customers.size() + i);
                    if (currentDistance < closestDepotDistance) {
                        closestDepotId = i;
                        closestDepotDistance = currentDistance;
                    }*/
                }
                //else find the closest depot to the last vehicle in the route
                else {
                    double currentDistance = neightbourMatrix.get(route.get(route.size() - 1)).get(this.customers.size() + i);
                    if (currentDistance < closestDepotDistance) {
                        closestDepotId = i;
                        closestDepotDistance = currentDistance;
                    }
                }
            }
            route.add(closestDepotId);
        }
        this.updateFitness();
    }

    public void updateFitness() {
        for (int routeID = 0; routeID < DNAString.size(); routeID++) {
            List<Integer> route = this.DNAString.get(routeID);
            this.punnishment += Math.max(0,
                    calculateRouteLength(route, this.vehicles.get(routeID).getDepotID()) - this.vehicles.get(routeID).getMaxDuration());
        }
        this.punnishment *= this.punnishmentRate;
        this.totalDistance = this.calculateFitness();
        this.fitness = this.totalDistance + this.punnishment;
    }


    public void addCustomer(int customerId, List<Integer> possibleVehicles) {
        Customer customer = customers.get(customerId);
        if (possibleVehicles.size() == 0) {
            throw new IllegalStateException("No more room in any vehicles (No room for weight in any vehicle)");
        }
        int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, possibleVehicles.size());
        int randomVehicle = possibleVehicles.get(randomVehicleIdx);
        double routeWeight = this.testRouteWeight(this.DNAString.get(randomVehicle), customer);
        if (routeWeight < vehicleWeights.get(randomVehicle)) {
            this.DNAString.get(randomVehicle).add(customer.getCustomerID());
        } else {
            possibleVehicles.remove(randomVehicleIdx);
            addCustomer(customerId, possibleVehicles);
        }
    }

    public void printMatrix(List<List<Integer>> mat) {
        for (List<Integer> route : mat) {
            String resultString = "";
            for (int value : route) {
                resultString += (" " + Integer.toString(value));
            }
            System.out.println(resultString);
        }
    }


    private double calculateFitness() {
        double fitness = 0;
        for (int i = 0; i < this.DNAString.size(); i++) {
            List<Integer> route = this.DNAString.get(i);
            fitness += calculateRouteLength(route, this.vehicles.get(i).getDepotID());
        }
        return fitness;
    }

    private double calculateRouteLength(List<Integer> route, int startDepotId) {
        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previous = this.customers.size() - 1 + startDepotId;
        //Adds distance between all customers
        for (int i = 0; i < route.size() - 1; i++) {
            int current = route.get(i);
            distance += neightbourMatrix.get(previous).get(current);
            previous = current;
        }
        //Adds distance from last customer to end depot
        int current = this.customers.size() - 1 + route.get(route.size() - 1);
        distance += neightbourMatrix.get(previous).get(current);
        return distance;
    }

    private double calculateRouteLength2(List<Integer> route, int startDepotId){
        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previousx = depots.get(startDepotId).getX();
        int previousy = depots.get(startDepotId).getY();
        //Adds distance between all customers
        for (int i = 0; i < route.size() - 1; i++) {
            int current = route.get(i);
            int currentx = customers.get(i).getX();
            int currenty = customers.get(i).getY();
            distance += calculateDistance(currentx, currenty, previousx, previousy);
            previousx = currentx;
            previousy = currenty;
        }
        //Adds distance from last customer to end depot
        int current = this.customers.size() - 1 + route.get(route.size() - 1);
        return distance;
    }


    private double testRouteLength(List<Integer> route, int startDepotId, int newCustomer) {

        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previous = this.customers.size() - 1 + startDepotId;
        //Calculates the distance from start depot to the new customer
        int current = newCustomer;
        distance += neightbourMatrix.get(previous).get(current);
        previous = current;
        //Calutes distances between all alread added customers
        for (int i = 0; i < route.size() - 1; i++) {
            current = route.get(i);
            distance += neightbourMatrix.get(previous).get(current);
            previous = current;
        }
        return distance;
    }

    private double testRouteWeight(List<Integer> route, Customer newCustomer) {
        double totalWeight = 0;
        for (int i = 0; i < route.size(); i++) {
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

    private double calculateDistance(int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow(x1-x2,2)+Math.pow(y1-y2,2));
    }
}