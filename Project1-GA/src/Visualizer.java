import java.awt.*;
import java.sql.SQLOutput;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class Visualizer extends JFrame {

    private MyCanvas canvas = new MyCanvas();


    public Visualizer(Map<Integer, Depot> depot_dict, Map<Integer, Customer> customer_dict,
                      Map<Integer, Vehicle> vehicle_dict, List<List<Integer>> DNAString, double maxCoordinate) {
        canvas.customer_dict = customer_dict;
        canvas.depot_dict = depot_dict;
        canvas.vehicle_dict = vehicle_dict;
        canvas.DNAString = DNAString;
        canvas.maxCoordinate = maxCoordinate;
        setLayout(new BorderLayout());
        setSize(720, 720);
        setTitle("Visualization");
        add("Center", canvas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void changeDNA(List<List<Integer>> DNAString){
        canvas.setDNA(DNAString);
    }

    //TODO Make dynamic
    private class MyCanvas extends Canvas {

        private Map<Integer, Depot> depot_dict;
        private Map<Integer, Customer> customer_dict;
        private Map<Integer, Vehicle> vehicle_dict;
        private List<List<Integer>> DNAString;
        private double maxCoordinate;
        List<Color> depotColours = new ArrayList<Color>(Arrays.asList(
                Color.blue, Color.red, Color.green, Color.yellow, Color.black, Color.pink, Color.cyan, Color.magenta, Color.orange, Color.lightGray));


        public void setDNA(List<List<Integer>> dna) {
            this.DNAString = dna;
        }

        private void drawPoint(Graphics g, int x, int y, Color color, int size) {
            g.setColor(color);

            // x*this.getWidth()/100 and this.getHeight()-y*this.getHeight()/100
            // to transform from 0-100 coordinates to screen coordinates
            g.fillOval((x + (int)(maxCoordinate+10)) * this.getWidth() / (int)(2*(maxCoordinate+10))-size/2, this.getHeight() - (y + (int)(maxCoordinate+10)) * this.getHeight() / (int)(2*(maxCoordinate+10)) - size/2, size, size);
        }

        private void drawLine(Graphics g, int x1, int y1, int x2, int y2, Color color) {
            g.setColor(color);

            // x*this.getWidth()/100 and this.getHeight()-y*this.getHeight()/100
            // to transform from 0-100 coordinates to screen coordinates
            g.drawLine((x1 + (int)(maxCoordinate+10)) * this.getWidth() / (int)(2*(maxCoordinate+10)), this.getHeight() - (y1 + (int)(maxCoordinate+10)) * this.getHeight() / (int)(2*(maxCoordinate+10)),
                    (x2 + (int)(maxCoordinate+10)) * this.getWidth() / (int)(2*(maxCoordinate+10)), this.getHeight() - (y2 + (int)(maxCoordinate+10)) * this.getHeight() / (int)(2*(maxCoordinate+10)));
        }

        @Override
        public void paint(Graphics g) {
            for (Depot depot : depot_dict.values()) {
                drawPoint(g, depot.getX(), depot.getY(), Color.gray, 13);
            }
            for (int i = 0; i < this.DNAString.size(); i++){
                Vehicle currentVehicle = vehicle_dict.get(i);
                List<Integer> currentRoute = this.DNAString.get(i);

                int startDepotId = currentVehicle.getDepotID();
                Color currentColor = depotColours.get(startDepotId);
                Depot startDepot = depot_dict.get(startDepotId);
                Depot endDepot = depot_dict.get(currentRoute.get(currentRoute.size()-1));
                Customer nextCustomer = customer_dict.get(currentRoute.get(0)-1);
                Customer lastCustomer;
                drawLine(g, startDepot.getX(), startDepot.getY(), nextCustomer.getX(), nextCustomer.getY(), currentColor);
                drawPoint(g, nextCustomer.getX(), nextCustomer.getY(), currentColor, 10);
                for(int j = 1; j < currentRoute.size()-2; j++){
                    lastCustomer = nextCustomer;
                    nextCustomer = customer_dict.get(currentRoute.get(j)-1);
                    drawLine(g, lastCustomer.getX(), lastCustomer.getY(), nextCustomer.getX(), nextCustomer.getY(), currentColor);
                    drawPoint(g, nextCustomer.getX(), nextCustomer.getY(), currentColor, 10);
                }
                    lastCustomer = nextCustomer;
                    drawLine(g, lastCustomer.getX(), lastCustomer.getY(), endDepot.getX(), endDepot.getY(), currentColor);
            }
        }

        public void changeDNA(List<List<Integer>> DNAString) {
            this.DNAString = DNAString;
            repaint();
        }
    }
}
