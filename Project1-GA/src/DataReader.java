import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class DataReader {
    private int number_of_vehicles_per_depot;
    private int number_of_costumers;
    private int number_of_depots;

    public DataReader() {
    }


    private String readFile(String name_of_file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(name_of_file));
        try {
            String line = br.readLine();
            int line_number = 1;

            while (line != null) {
                //System.out.println(line);
                String[] splitStr = line.trim().split("\\s+");
                // Reads the first line and stores information
                if (line_number == 1) {
                    this.number_of_vehicles_per_depot = Integer.parseInt(splitStr[0]);
                    this.number_of_costumers = Integer.parseInt(splitStr[1]);
                    this.number_of_depots = Integer.parseInt(splitStr[2]);
                }
                // Have enough information to create the vehicles after reading line two
                if (line_number == 2) {
                    for (int depot=0; depot < this.number_of_depots; depot++) {
                        for(int vehicle=0; vehicle < this.number_of_vehicles_per_depot; vehicle++) {
                            //Kaller lage bil
                        }
                    }
                }

                // Create the customers
                if (line_number > this.number_of_depots+1 && line_number < this.number_of_costumers + (this.number_of_depots+2)) {
                    //Call create customer
                    //System.out.println(" "+splitStr[0] + " " + splitStr[1] + " " + splitStr[2] + " " + splitStr[3] + " " + splitStr[4]);
                }

                // Create depots
                if (line_number > this.number_of_costumers + (this.number_of_depots+1)) {
                    System.out.println(" "+splitStr[0] + " " + splitStr[1] + " " + splitStr[2]);
                }


                line_number += 1;
                line = br.readLine();
            }
            return "Finito";
        } finally {
            br.close();
        }
    }

    public static void main(String[] args) throws IOException {
        DataReader dr = new DataReader();
        dr.readFile("src/p01");
    }

}

