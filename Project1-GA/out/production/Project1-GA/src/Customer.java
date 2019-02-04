public class Customer {
    private int x;
    private int y;
    private int serviceDuration;
    private int demand;

    public Customer(int x, int y, int serviceDuration, int demand) {
        this.x = x;
        this.y = y;
        this.serviceDuration = serviceDuration;
        this.demand = demand;
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

    public int getDemand() {
        return demand;
    }
}