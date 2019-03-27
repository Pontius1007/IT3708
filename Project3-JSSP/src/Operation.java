public class Operation implements Comparable<Operation> {
    public int jobId;
    public int machineId;
    public double position;
    public double velocity;

    public Operation(int jobId, int machineId, double position, double velocity) {
        this.jobId = jobId;
        this.machineId = machineId;
        this.position = position;
        this.velocity = velocity;
    }

    public Operation(int jobId, int machineId, double position) {
        this.jobId = jobId;
        this.machineId = machineId;
        this.position = position;
    }

    @Override
    public int compareTo(Operation o) {
        if (this.position < o.position) return -1;
        else if (this.position > o.position) return 1;
        return 0;
    }
}
