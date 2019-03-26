import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Schedule {
    public List<List<Integer>> schedule;
    public int makespan;

    public Schedule(Operation[] particle) {
        for(int i = 0; i < LookupTable.numberOfMachines; i++){
            schedule.add(new ArrayList<>());
        }
            Operation[] operations = particleCopy(particle);
        Arrays.sort(operations);

        for(Operation operation : operations){
            addJobToSchedule(operation);
        }
    }

    private void addJobToSchedule(Operation operation){
        int jobId = operation.jobId;
        int machineId = operation.machineId;
        int startTime = 0;
        for(List<Integer> machine: schedule){
            for(int time = 0; time < machine.size(); time++){
                if(machine.get(time) == jobId){
                    startTime = time;
                }
            }
        }
        for(int i = machineId; machineId < startTime; machineId++){
            schedule.get(machineId).add(-1);
        }
    }

    private Operation[] particleCopy(Operation[] particle){
        Operation[] copy = new Operation[particle.length];
        for(int jobIdx = 0; jobIdx < particle.length; jobIdx++){
            Operation copyOperation = particle[jobIdx];
            copy[jobIdx] = new Operation(copyOperation.jobId, copyOperation.machineId, copyOperation.position);
        }
        return copy;
    }
}
