import java.awt.*;
import java.sql.SQLOutput;
import java.util.Map;
import javax.swing.*;

public class Visualizer extends JFrame{

    private MyCanvas canvas = new MyCanvas();


    public Visualizer(Map<Integer, Depot> depot_dict, Map<Integer, Customer> customer_dict){
        canvas.setCustomer_dict(customer_dict);
        canvas.setDepot_dict(depot_dict);
        setLayout(new BorderLayout());
        setSize(720, 720);
        setTitle("Visualization");
        add("Center", canvas);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    //TODO Make dynamic
    private class MyCanvas extends Canvas{

        private Map<Integer, Depot> depot_dict;
        private Map<Integer, Customer> customer_dict;
        private DNA dna;

        public void setDepot_dict(Map<Integer, Depot> depot_dict){
            this.depot_dict = depot_dict;
        }

        public void setCustomer_dict(Map<Integer, Customer> customer_dict){
            this.customer_dict = customer_dict;
        }

        public void setDNA(DNA dna){
            this.dna = dna;
        }

        private void drawPoint(Graphics g, int x, int y, Color color, int size){
            g.setColor(color);

            // x*this.getWidth()/100 and this.getHeight()-y*this.getHeight()/100
            // to transform from 0-100 coordinates to screen coordinates
            g.fillOval(x*this.getWidth()/100, this.getHeight()-y*this.getHeight()/100, size, size);
        }

        private void drawLine(Graphics g, int x1, int y1, int x2, int y2, Color color) {
            g.setColor(color);

            // x*this.getWidth()/100 and this.getHeight()-y*this.getHeight()/100
            // to transform from 0-100 coordinates to screen coordinates
            g.drawLine(x1*this.getWidth()/100, this.getHeight()-y1*this.getHeight()/100,
                    x2*this.getWidth()/100, this.getHeight()-y2*this.getHeight()/100);
        }

        @Override
        public void paint(Graphics g){
            for(Depot depot: depot_dict.values()){
                drawPoint(g, depot.getX(), depot.getY(), Color.gray, 13);
            }
            for(Customer customer: this.customer_dict.values()){
                drawPoint(g, customer.getX(), customer.getY(), Color.black, 10);
            }
        }
    }
}
