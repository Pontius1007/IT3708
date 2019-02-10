public class Customer {
    private int customerID;
    private int x;
    private int y;
    private int serviceDuration;
    private int weight;
    private int closestDepotID;

    public Customer(int x, int y, int serviceDuration, int weight, int customerID) {
        this.x = x;
        this.y = y;
        this.serviceDuration = serviceDuration;
        this.weight = weight;
        this.customerID = customerID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public int getWeight() {
        return weight;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getClosestDepotID() {
        return closestDepotID;
    }

    public void setClosestDepotID(int closestDepotID) {
        this.closestDepotID = closestDepotID;
    }
}