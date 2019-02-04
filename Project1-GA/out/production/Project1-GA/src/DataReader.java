import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataReader {
    private int number_of_vehicles_per_depot;
    private int number_of_costumers;
    private int number_of_depots;

    private Map<Integer, Vehicle> vehicle_dict = new HashMap<Integer, Vehicle>();
    private Map<Integer, Customer> customer_dict = new HashMap<Integer, Customer>();
    private Map<Integer, Depot> depot_dict = new HashMap<Integer, Depot>();

    private DataReader() {
    }


    //TODO: Create print for each dict
    private void readFile(String name_of_file) throws IOException {
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
                int maxDuration = Integer.parseInt(splitStr[0]);
                int maxLoad = Integer.parseInt(splitStr[1]);
                for (int vehicle = 0; vehicle < this.number_of_vehicles_per_depot; vehicle++) {
                    int veID = (depot * this.number_of_vehicles_per_depot + vehicle) + 1;
                    vehicle_dict.put(veID, new Vehicle(depot, veID, maxDuration, maxLoad));
                }
            }

            // Create the customers
            for (int customer_id = 1; customer_id <= this.number_of_costumers; customer_id++) {
                line = br.readLine();
                splitStr = line.trim().split("\\s+");
                customer_dict.put(Integer.parseInt(splitStr[0]), new Customer(Integer.parseInt(splitStr[1]),
                        Integer.parseInt(splitStr[2]), Integer.parseInt(splitStr[3]), Integer.parseInt(splitStr[4])));
            }

            // Create depots
            for (int depot_id = 1; depot_id <= this.number_of_depots; depot_id++) {
                line = br.readLine();
                splitStr = line.trim().split("\\s+");
                depot_dict.put(depot_id, new Depot(Integer.parseInt(splitStr[1]), Integer.parseInt(splitStr[2])));
            }
        } finally {
            br.close();
        }
    }

    public static void main(String[] args) throws IOException {
        DataReader dr = new DataReader();
        dr.readFile("data/Data Files/p04");
        Visualizer vis = new Visualizer(dr.depot_dict, dr.customer_dict);
    }
}

