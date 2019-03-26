import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class LookupTable {
    public static List<List<List<Integer>>> jobSchedule = new ArrayList<>();
    public static int numberOfJobs;
    public static int numberOfMachines;


    public LookupTable() {}


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

        }
        finally {
            br.close();
        }

    }

    private List<List<Integer>> convertToInt(Collection<List<String>> toBeConverted) {
        List<List<Integer>> converted = new ArrayList<>();
        for(List<String> couple : toBeConverted) {
            List<Integer> temp = new ArrayList<>();
            for(String value : couple) {
                temp.add(Integer.parseInt(value));
            }
            converted.add(temp);
        }
        return converted;
    }

    private static  <T> Collection<List<T>> partition(List<T> list, int size) {
        final AtomicInteger counter = new AtomicInteger(0);

        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

    public static void main(String[] args) throws IOException {
        LookupTable test = new LookupTable();
        test.readFile("1");
        for(List<List<Integer>> temp : test.jobSchedule) {
            System.out.println(temp);
        }
    }

}
