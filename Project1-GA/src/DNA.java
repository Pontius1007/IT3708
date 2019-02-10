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
    private double punnishmentRate = 3;

    public static List<List<Double>> neightbourMatrix;
    public static Map<Integer, Vehicle> vehicles;
    public static Map<Integer, Depot> depots;
    public static Map<Integer, Customer> customers;

    public DNA() {
        this.DNAString = new ArrayList<>();
        this.vehicleWeights = new ArrayList<>();
        //initializeDnaRandomly();
        initialzeSmart();
    }


    public DNA(List<List<Integer>> DNAString){
        this.DNAString = DNAString;
        this.vehicleWeights = new ArrayList<>();
        for (int x = 0; x < this.vehicles.size(); x++) {
            this.vehicleWeights.add(this.vehicles.get(x).getMaxLoad());
        }
        this.updateFitness();
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

    public void initialzeSmart(){
        //Loope over kunder, sjekke for vekt og legge til
        for (int x = 0; x < this.vehicles.size(); x++) {
            this.DNAString.add(new ArrayList<>());
            this.vehicleWeights.add(this.vehicles.get(x).getMaxLoad());
        }

        //Adds all customers to a vehicle
        for (int i = 0; i < customers.size(); i++) {
            List<Integer> possibleVehiclesSmart = new ArrayList<>();
            Customer customer = customers.get(i);
            int closestDepot = customer.getClosestDepotID();
            int vehiclesPerDepot = this.vehicles.size()/this.depots.size();
            for (int x = closestDepot*vehiclesPerDepot; x < (closestDepot+1)*vehiclesPerDepot; x++) {
                possibleVehiclesSmart.add(x);
            }
            addSmartCustomer(i, possibleVehiclesSmart, closestDepot, vehiclesPerDepot);
        }

        this.addEndDepots();
        /*for (Integer intest: this.vehicleWeights) {
            System.out.println(intest);
        }*/
    }

    public void addSmartCustomer(int customerId, List<Integer> possibleVehiclesSmart, int closestDepot, int vehiclesPerDepot) {

        if (possibleVehiclesSmart.size() == 0) {
            int randomVehicleIdxInClosestDepot = ThreadLocalRandom.current().nextInt(closestDepot*vehiclesPerDepot, (closestDepot+1)*vehiclesPerDepot);
            this.DNAString.get(randomVehicleIdxInClosestDepot).add(customerId);
        }
        else {
            int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, possibleVehiclesSmart.size());
            int randomVehicle = possibleVehiclesSmart.get(randomVehicleIdx);
            Customer customer = customers.get(customerId);
            double routeWeight = this.testRouteWeight(this.DNAString.get(randomVehicle), customer);
            double routeLength = this.testRouteLength(this.DNAString.get(randomVehicle), vehicles.get(randomVehicle).getDepotID(), randomVehicle);
            if (routeWeight < vehicleWeights.get(randomVehicle) && routeLength < vehicles.get(randomVehicle).getMaxDuration()) {
                this.DNAString.get(randomVehicle).add(customerId);
            } else {
                possibleVehiclesSmart.remove(randomVehicleIdx);
                addSmartCustomer(customerId, possibleVehiclesSmart, closestDepot, vehiclesPerDepot);
            }
        }
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
            List<Integer> route = DNAString.get(routeID);
            if(route.size() == 0){
                route.add(vehicles.get(routeID).getDepotID());
            }
            else{
                route.add(customers.get(route.get(route.size()-1)).getClosestDepotID());
            }
        }
        this.updateFitness();
    }

    public void updateFitness() {
        this.punnishment = 0;
        for (int routeID = 0; routeID < DNAString.size(); routeID++) {
            List<Integer> route = this.DNAString.get(routeID);
            this.punnishment += Math.max(0,
                    calculateRouteLength(route, this.vehicles.get(routeID).getDepotID()) - this.vehicles.get(routeID).getMaxDuration());
            this.punnishment += Math.max(0,
                    calculateRouteWeight(route) - this.vehicles.get(routeID).getMaxLoad());
        }
        this.punnishment *= this.punnishmentRate;
        this.totalDistance = this.calculateFitness();
        this.fitness = this.totalDistance + this.punnishment;
    }


    public void addCustomer(int customerId, List<Integer> possibleVehicles) {
        Customer customer = customers.get(customerId);
        //System.out.println(possibleVehicles);
        //printMatrix(getDNAString());
        if (possibleVehicles.size() == 0) {
            int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, vehicles.size()-1);
            this.DNAString.get(randomVehicleIdx).add(customer.getCustomerID());
        }
        else{
            int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, possibleVehicles.size());
            int randomVehicle = possibleVehicles.get(randomVehicleIdx);
            double routeWeight = this.testRouteWeight(this.DNAString.get(randomVehicle), customer);
            double routeLength = this.testRouteLength(this.DNAString.get(randomVehicle), vehicles.get(randomVehicle).getDepotID(), randomVehicle);
            if (routeWeight < vehicleWeights.get(randomVehicle) && routeLength < vehicles.get(randomVehicle).getMaxDuration()) {
                this.DNAString.get(randomVehicle).add(customer.getCustomerID());
            } else {
                possibleVehicles.remove(randomVehicleIdx);
                addCustomer(customerId, possibleVehicles);
            }
        }
    }

    public void insertCustomer(int customerId, List<Integer> possibleVehicles) {
        for (List<Integer> route : this.getDNAString()) {
            route.remove(route.size() - 1);
        }
        Customer customer = customers.get(customerId);
        //System.out.println(possibleVehicles);
        //printMatrix(getDNAString());
        if (possibleVehicles.size() == 0) {
            int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, vehicles.size()-1);
            if(this.DNAString.get(randomVehicleIdx).size()==0){
                this.DNAString.get(randomVehicleIdx).add(customer.getCustomerID());
            }
            this.DNAString.get(randomVehicleIdx).add(
                    ThreadLocalRandom.current().nextInt(0, this.DNAString.get(randomVehicleIdx).size()+1), customer.getCustomerID());
        }
        else{
            int randomVehicleIdx = ThreadLocalRandom.current().nextInt(0, possibleVehicles.size());
            int randomVehicle = possibleVehicles.get(randomVehicleIdx);
            double routeWeight = this.testRouteWeight(this.DNAString.get(randomVehicle), customer);
            if (routeWeight < vehicleWeights.get(randomVehicle)) {
                if(this.DNAString.get(randomVehicleIdx).size()==0){
                    this.DNAString.get(randomVehicle).add(0, customer.getCustomerID());
                }
                else{
                    this.DNAString.get(randomVehicle).add(
                            ThreadLocalRandom.current().nextInt(0, this.DNAString.get(randomVehicleIdx).size()+1), customer.getCustomerID());
                }
            } else {
                possibleVehicles.remove(randomVehicleIdx);
                addCustomer(customerId, possibleVehicles);
            }
        }
        this.addEndDepots();
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

    public double calculateRouteLength(List<Integer> route, int startDepotId) {
        if(route.size()> 1){
            double distance = 0;
            //This is the index of the starting depot in the neighbour matrix
            int previous = this.customers.size() + startDepotId;
            //Adds distance between all customers
            for (int i = 0; i < route.size() - 1; i++) {
                int current = route.get(i);
                distance += neightbourMatrix.get(previous).get(current);
                previous = current;
            }
            //Adds distance from last customer to end depot
            int current = this.customers.size() + route.get(route.size() - 1);
            distance += neightbourMatrix.get(previous).get(current);
            return distance;
        }
        else{
            return 0;
        }

    }


    private double testRouteLength(List<Integer> route, int startDepotId, int newCustomer) {

        double distance = 0;
        //This is the index of the starting depot in the neighbour matrix
        int previous = this.customers.size() + startDepotId;
        //Calculates the distance from start depot to the new customer
        int current;
        //Calutes distances between all alread added customers
        for (int i = 0; i < route.size() - 1; i++) {
            current = route.get(i);
            distance += neightbourMatrix.get(previous).get(current);
            previous = current;
        }
        current = newCustomer;
        distance += neightbourMatrix.get(previous).get(current);
        return distance;
    }

    public double calculateRouteWeight(List<Integer> route){
        double totalWeight = 0;
        for (int i = 0; i < route.size()-1; i++) {
            double weight = this.customers.get(route.get(i)).getWeight();
            totalWeight += weight;
        }
        return totalWeight;
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

}