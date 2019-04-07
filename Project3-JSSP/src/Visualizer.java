import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

public class Visualizer extends JFrame{

    public Visualizer(String title, Schedule plottSchedule) {
        super(title);

        //Create the dataset
        IntervalCategoryDataset dataset = createDatasetFromSchedule(plottSchedule);

        // Create chart
        JFreeChart chart = ChartFactory.createGanttChart(
                title, // Chart title
                "Machine Jobs", // X-Axis Label
                "Scheduling", // Y-Axis Label
                dataset);

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    private IntervalCategoryDataset createDatasetFromSchedule(Schedule plottSchedule) {
        TaskSeriesCollection dataset = new TaskSeriesCollection();

        int jobNumber = 0;
        //Loops over every machine in the schedule
        for (List<Integer> machine : plottSchedule.schedule) {
            TaskSeries series1 = new TaskSeries("Job " + jobNumber);
            Task job0 = new Task("Job 0", date(0), date(0));


            int start = 0;
            int duration = 0;
            int startJobNumber = machine.get(0);
            boolean toPlotOrNotToPlot = false;
            boolean firstJob = true;
            for (int i = 0; i < machine.size(); i++) {
                if(machine.get(i) != 0) {
                    if(machine.get(i) != startJobNumber) {
                        startJobNumber = machine.get(i);
                        toPlotOrNotToPlot = !toPlotOrNotToPlot;

                        if(toPlotOrNotToPlot) {
                            job0.addSubtask(new Task("Test", date(start), date(start+duration)));
                        }
                        duration = 0;
                        start = i;

                    }
                    else {
                        duration++;
                    }
                }

            }
            series1.add(job0);
            dataset.add(series1);

            break;

        }


        return dataset;
    }

    private Date date(int hour) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.APRIL, 1, hour, 0, 0);
        return calendar.getTime();
    }
}