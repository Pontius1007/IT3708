import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LookupTable {
    public static List<List<List<Integer>>> jobSchedule = new ArrayList<>();
    public static int numberOfJobs;
    public static int numberOfMachines;


    public static int[][] durations;
    public static int[][] jobOrder;

    public LookupTable() {
    }

    private static <T> Collection<List<T>> partition(List<T> list, int size) {
        final AtomicInteger counter = new AtomicInteger(0);

        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

    public static void main(String[] args) throws IOException {
        LookupTable test = new LookupTable();
        test.readFile("1");
        for (List<List<Integer>> temp : jobSchedule) {
            System.out.println(temp);
        }
    }

    public void readFile(String fileName) throws IOException {
        String fileNameCorrect = "Test Data/" + fileName + ".txt";
        BufferedReader br = new BufferedReader(new FileReader(fileNameCorrect));
        try {
            //Reads first line and sets number of jobs and machines
            String line = br.readLine();
            String[] numberJobsAndMachines = line.trim().split("\\s+");
            LookupTable.numberOfJobs = Integer.parseInt(numberJobsAndMachines[0]);
            LookupTable.numberOfMachines = Integer.parseInt(numberJobsAndMachines[1]);

            String[] splitStr;
            for (int i = 0; i < LookupTable.numberOfJobs; i++) {
                line = br.readLine();
                splitStr = line.trim().split("\\s+");
                List<String> readLinesAsList = Arrays.asList(splitStr);
                Collection<List<String>> jobAs2D = partition(readLinesAsList, 2);
                jobSchedule.add(convertToInt(jobAs2D));
            }
        } finally {
            durations = new int[numberOfJobs][numberOfMachines];
            jobOrder = new int[numberOfJobs][numberOfMachines];
            for (int jobId = 0; jobId < numberOfJobs; jobId++) {
                List<List<Integer>> row = jobSchedule.get(jobId);
                for (int machineId = 0; machineId < numberOfMachines; machineId++) {
                    List<Integer> tuple = row.get(machineId);
                    durations[jobId][tuple.get(0)] = tuple.get(1);
                    jobOrder[jobId][machineId] = tuple.get(0);

                }
            }
            br.close();
        }

    }

    private List<List<Integer>> convertToInt(Collection<List<String>> toBeConverted) {
        List<List<Integer>> converted = new ArrayList<>();
        for (List<String> couple : toBeConverted) {
            List<Integer> temp = new ArrayList<>();
            for (String value : couple) {
                temp.add(Integer.parseInt(value));
            }
            converted.add(temp);
        }
        return converted;
    }
}
