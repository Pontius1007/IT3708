import java.util.*;

public class Schedule {
    // Schedule is one row for each machine, 1 element is one time unit
    // -1 if the machine is waiting, and number of job being executed if working.
    public List<List<Integer>> schedule;
    public int makespan = 0;
    public int[] lastOperation;

    public Schedule(Operation[] particle) {
        schedule = new ArrayList<>();
        lastOperation = new int[LookupTable.numberOfJobs];
        // lastOperation for tracking the last machine that did job(i)
        for (int i = 0; i < LookupTable.numberOfJobs; i++) lastOperation[i] = -1;
        for (int i = 0; i < LookupTable.numberOfMachines; i++) {
            schedule.add(new ArrayList<>());
        }
        // copying the particle array to not fuck up references when sorting
        List operations = particleCopy(particle);
        Collections.sort(operations);
        // add all operations to the schedule
        while (operations.size() > 0) {
            Operation best = bestlegal(operations);
            addOperationToSchedule(best);
            operations.remove(best);
        }
        // find the longest time a machine uses, and use it as makespan
        for (int machineId = 0; machineId < LookupTable.numberOfMachines; machineId++) {
            int size = schedule.get(machineId).size();
            if (size > makespan) {
                makespan = size;
            }
        }
    }

    // finds the first operation that can be inserted with the constrains of machine ordering
    private Operation bestlegal(List<Operation> operations) throws IllegalStateException {
        List<Map<Integer, Integer>> previousMachine = LookupTable.previousMachine;
        for (Operation o : operations) {
            int requiredPrevious = previousMachine.get(o.jobId).get(o.machineId);
            if (requiredPrevious == -1 || requiredPrevious == lastOperation[o.jobId]) {
                return o;
            }
        }
        throw new IllegalStateException("Not possible to add any Operations");
    }

    // if the job is currently being done by another machine, the machine waits for it to be done, and then executes
    private void addOperationToSchedule(Operation operation) {
        int jobId = operation.jobId;
        int machineId = operation.machineId;
        int startTime = 0;
        // finds the latest time that the job was executed
        for (List<Integer> machine : schedule) {
            for (int time = 0; time < machine.size(); time++) {
                if (machine.get(time) == jobId && time + 1 > startTime) {
                    startTime = time + 1;
                }
            }
        }
        // adds -1s to wait for the last time the job was executed by another machine
        for (int i = schedule.get(machineId).size(); i < startTime; i++) {
            schedule.get(machineId).add(-1);
        }
        // then adds the current job with respective duration to the machine
        int joblen = LookupTable.durations[jobId][machineId];
        for (int i = 0; i < joblen; i++) {
            schedule.get(machineId).add(jobId);
        }
        lastOperation[jobId] = machineId;
    }

    private List<Operation> particleCopy(Operation[] particle) {
        List<Operation> copy = new ArrayList<>(particle.length);
        for (int jobIdx = 0; jobIdx < particle.length; jobIdx++) {
            copy.add(particle[jobIdx]);
        }
        return copy;
    }
}
