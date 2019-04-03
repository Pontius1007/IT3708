public class Operation implements Comparable<Operation> {
    public int jobId;
    public double position;
    public double velocity;

    public Operation(int jobId, double position, double velocity) {
        this.jobId = jobId;
        this.position = position;
        this.velocity = velocity;
    }

    @Override
    public int compareTo(Operation o) {
        if (this.position < o.position) return -1;
        else if (this.position > o.position) return 1;
        return 0;
    }
}
