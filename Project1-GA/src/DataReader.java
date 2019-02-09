import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DataReader {
    private int number_of_vehicles_per_depot;
    private int number_of_costumers;
    private int number_of_depots;
    private double targetFitness;

    public Map<Integer, Vehicle> vehicle_dict = new HashMap<Integer, Vehicle>();
    public Map<Integer, Customer> customer_dict = new HashMap<Integer, Customer>();
    public Map<Integer, Depot> depot_dict = new HashMap<Integer, Depot>();
    public List<List<Double>> neighBourMatrix = new ArrayList<>();
    public double maxCoordinate = 0;
    public double minCoordinate = 0;

    public DataReader() {
    }


    //TODO: Create print for each dict
    public void readFile(String name_of_file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(name_of_file));
        try {
            String line = br.readLine();
            String[] splitStr = line.trim().split("\\s+");

            // Reads the first line and stores information
            this.number_of_vehicles_per_depot = Integer.parseInt(splitStr[0]);
            this.number_of_costumers = Integer.parseInt(splitStr[1]);
            this.number_of_depots = Integer.parseInt(splitStr[2]);

            // Have enough information to create the vehicles after reading line two
            for (int depot = 0; depot < this.number_of_depots; depot++) {
                line = br.readLine();
                splitStr = line.trim().split("\\s+");
                int maxDistance = Integer.parseInt(splitStr[0]) == 0 ? Integer.MAX_VALUE: Integer.parseInt(splitStr[0]);
                int maxLoad = Integer.parseInt(splitStr[1]);
                for (int vehicle = 0; vehicle < this.number_of_vehicles_per_depot; vehicle++) {
                    int veID = (depot * this.number_of_vehicles_per_depot + vehicle);
                    vehicle_dict.put(veID, new Vehicle(depot, veID, maxDistance, maxLoad));
                }
            }

            // Create the customers
            for (int customer_id = 0; customer_id < this.number_of_costumers; customer_id++) {
                line = br.readLine();
                splitStr = line.trim().split("\\s+");
                Customer currentCustomer = new Customer(Integer.parseInt(splitStr[1]),
                        Integer.parseInt(splitStr[2]), Integer.parseInt(splitStr[3]), Integer.parseInt(splitStr[4]),
                        customer_id);
                customer_dict.put(customer_id, currentCustomer);
                this.maxCoordinate = Math.max(this.maxCoordinate, Math.max(currentCustomer.getX(), currentCustomer.getY()));
                this.minCoordinate = Math.min(this.minCoordinate, Math.min(currentCustomer.getX(), currentCustomer.getY()));
            }

            // Create depots
            for (int depot_id = 0; depot_id < this.number_of_depots; depot_id++) {
                line = br.readLine();
                splitStr = line.trim().split("\\s+");
                Depot currentDepot = new Depot(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2]));
                depot_dict.put(depot_id, currentDepot);
                this.maxCoordinate = Math.max(this.maxCoordinate, Math.max(currentDepot.getX(), currentDepot.getY()));
                this.minCoordinate = Math.min(this.minCoordinate, Math.min(currentDepot.getX(), currentDepot.getY()));
            }
        } finally {
            br.close();
            this.createNeighbourMatrix();
            DNA.neightbourMatrix = this.neighBourMatrix;
        }
    }

    public double resultReader (String name_of_file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(name_of_file))) {
            String line = br.readLine();
            String[] splitStr = line.trim().split("\\s+");
            this.targetFitness = Double.parseDouble(splitStr[0]);
        }
        return this.targetFitness;

    }

    private void createNeighbourMatrix() {
        DecimalFormat df = new DecimalFormat("#.00");
        for (int customer = 0; customer < this.customer_dict.size(); customer++) {
            this.neighBourMatrix.add(new ArrayList<Double>());
            //String resultline = "";
            for (int customer2 = 0; customer2 < this.customer_dict.size(); customer2++) {
                double distance = Math.sqrt(Math.pow(this.customer_dict.get(customer2).getX() - this.customer_dict.get(customer).getX(), 2) +
                        Math.pow(this.customer_dict.get(customer2).getY() - this.customer_dict.get(customer).getY(), 2));

                this.neighBourMatrix.get(customer).add(distance);
                //resultline += (" " + df.format(distance));
            }
            for (int depot = 0; depot < this.depot_dict.size(); depot++) {
                double distance = Math.sqrt(Math.pow(this.depot_dict.get(depot).getX() - this.customer_dict.get(customer).getX(), 2) +
                        Math.pow(this.depot_dict.get(depot).getY() - this.customer_dict.get(customer).getY(), 2));
                this.neighBourMatrix.get(customer).add(distance);
                //resultline += (" " + df.format(distance));
            }
            //System.out.println(resultline);
        }
        for (int depot = 0; depot < this.depot_dict.size(); depot++) {
            String resultline = "";
            this.neighBourMatrix.add(new ArrayList<Double>());

            for (int customer2 = 0; customer2 < this.customer_dict.size(); customer2++) {
                double distance = Math.sqrt(Math.pow(this.customer_dict.get(customer2).getX() - this.depot_dict.get(depot).getX(), 2) +
                        Math.pow(this.customer_dict.get(customer2).getY() - this.depot_dict.get(depot).getY(), 2));

                this.neighBourMatrix.get(customer_dict.size()+depot).add(distance);
                //resultline += (" " + df.format(distance));

            }
            for (int depot2 = 0; depot2 < this.depot_dict.size(); depot2++) {
                double distance = Math.sqrt(Math.pow(this.depot_dict.get(depot).getX() - this.customer_dict.get(depot2).getX(), 2) +
                        Math.pow(this.depot_dict.get(depot).getY() - this.customer_dict.get(depot2).getY(), 2));
                this.neighBourMatrix.get(customer_dict.size()+depot).add(distance);
            }
            //System.out.println(resultline);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        /*
        DataReader dr = new DataReader();
        dr.readFile("data/Data Files/p01");
        DNA dna = new DNA(dr.vehicle_dict, dr.depot_dict, dr.customer_dict);
        Visualizer vis = new Visualizer(dr.depot_dict, dr.customer_dict, dr.vehicle_dict, dna.getDNAString(), dr.maxCoordinate, dr.minCoordinate);
        TimeUnit.SECONDS.sleep(6);
        DNA dna2 = new DNA(dr.vehicle_dict, dr.depot_dict, dr.customer_dict);
        vis.setVisible(false);
        vis.dispose();
        vis = new Visualizer(dr.depot_dict, dr.customer_dict, dr.vehicle_dict, dna2.getDNAString(), dr.maxCoordinate, dr.minCoordinate);
        */

    }
}

