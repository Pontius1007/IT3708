package src;

public class Vehicle {
    private int depotID;
    private int vehicleID;
    private int maxDuration;
    private int maxLoad;

    public Vehicle(int depotID, int svehicleID, int maxDuration, int maxLoad) {
        this.depotID = depotID;
        this.maxDuration = maxDuration;
        this.maxLoad = maxLoad;
    }

    public int getDepotID() {
        return depotID;
    }

    public int getVehicleID() {
        return vehicleID;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public int getMaxLoad() {
        return maxLoad;
    }
}
