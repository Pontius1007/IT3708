import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Schedule {
    // Schedule is one row for each machine, 1 element is one time unit
    // -1 if the machine is waiting, and number of job being executed if working.
    public List<List<Integer>> schedule;
    public int makespan = 0;
    private int[] lastOperation;
    private int[] machineNumber;

    public Schedule(Operation[] particle) {
        schedule = new ArrayList<>();
        // lastOperation keeps track of last time a machine is executing each job
        lastOperation = new int[LookupTable.numberOfJobs];
        // machineNumber keeps track of the current machineNumber doing the specified job
        machineNumber = new int[LookupTable.numberOfJobs];
        // lastOperation for tracking the last time a machine did job(i)
        for (int i = 0; i < LookupTable.numberOfJobs; i++) lastOperation[i] = -1;
        for (int i = 0; i < LookupTable.numberOfMachines; i++) {
            schedule.add(new ArrayList<>());
        }

        // copying the particle array to not fuck up references when sorting
        List<Operation> operations = particleCopy(particle);
        Collections.sort(operations);
        // add all operations to the schedule
        for(Operation o: operations){
            addOperationToSchedule(o);
        }
        // find the longest time a machine uses, and use it as makespan
        for (int machineId = 0; machineId < LookupTable.numberOfMachines; machineId++) {
            int size = schedule.get(machineId).size();
            if (size > makespan) {
                makespan = size;
            }
        }
        for(List<Integer> machine: schedule){
            for(int i = 0; i < machine.size(); i++){
                machine.set(i, machine.get(i)+1);
            }
        }
    }


    // if the job is currently being done by another machine, the machine waits for it to be done, and then executes
    private void addOperationToSchedule(Operation operation) {
        int jobId = operation.jobId;
        int machineId = LookupTable.jobOrder[jobId][machineNumber[jobId]++];

        int startTime = lastOperation[jobId];

        // waits for the job to finnish on the other machine
        for(int waitingTime = schedule.get(machineId).size(); waitingTime < startTime; waitingTime ++){
            schedule.get(machineId).add(-1);
        }
        // then adds the current job with respective duration to the machine
        int joblen = LookupTable.durations[jobId][machineId];
        for (int i = 0; i < joblen; i++) {
            schedule.get(machineId).add(jobId);
        }
        lastOperation[jobId] = schedule.get(machineId).size();
    }

    private List<Operation> particleCopy(Operation[] particle) {
        List<Operation> copy = new ArrayList<>(particle.length);
        for (int jobIdx = 0; jobIdx < particle.length; jobIdx++) {
            copy.add(particle[jobIdx]);
        }
        return copy;
    }
}
