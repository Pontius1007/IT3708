import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

public class Visualizer extends JFrame {
    public static final TaskSeriesCollection model = new TaskSeriesCollection();

    public Visualizer(String title, Schedule plottSchedule) {
        super(title);

        //Create the dataset
        TaskSeriesCollection dataset = createDatasetFromSchedule(plottSchedule);

        // Create chart
        JFreeChart chart = ChartFactory.createGanttChart(
                title, // Chart title
                "Machine Jobs", // X-Axis Label
                "Scheduling", // Y-Axis Label
                dataset,
                true,
                true,
                false
        );

        final CategoryPlot plot = (CategoryPlot) chart.getPlot();
        DateAxis range = (DateAxis) plot.getRangeAxis();
        range.setDateFormatOverride(new SimpleDateFormat("SSS"));
        range.setMaximumDate(new Date(100));

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

        //GanttRenderer personnalis..
        MyRenderer renderer = new MyRenderer(model);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.WHITE);

        //Loops over subtasks
        for(int row = 0; row < dataset.getRowCount(); row++) {
            for(int col = 0; col < dataset.getColumnCount(); col++) {
                System.out.println(dataset.getSubIntervalCount(row, col));
            }
        }
    }

    private static class MyRenderer extends GanttRenderer {
        private final Color[] colorList = getUniqueColors(6);
        private final TaskSeriesCollection model;
        private int row;
        private int col;
        private int index;

        public MyRenderer(TaskSeriesCollection model) {
            this.model = model;
        }

        @Override
        public Paint getItemPaint(int row, int col) {
            System.out.println(row + " " + col + " " + super.getItemPaint(row, col));
            return colorList[2];
        }

        private void getTaskId(int row, int col) {
            TaskSeries series = (TaskSeries) model.getRowKeys().get(row);
            List<Task> tasks = series.getTasks(); // unchecked

            int taskCount = tasks.get(col).getSubtaskCount();
            taskCount = Math.max(1, taskCount);

            System.out.println("----> " + taskCount);
        }

        //Method for generating a list of unique colors. From: https://stackoverflow.com/questions/3403826/how-to-dynamically-compute-a-list-of-colors
        Color[] getUniqueColors(int amount) {
            Color[] cols = new Color[amount];
            for (int i = 0; i < amount; i++) {
                cols[i] = Color.getHSBColor((float) i / amount, 1, 1);
            }
            return cols;
        }
    }



    private TaskSeriesCollection createDatasetFromSchedule(Schedule plottSchedule) {
        TaskSeriesCollection dataset = new TaskSeriesCollection();

        int jobNumber = 0;
        //Loops over every machine in the schedule
        for (List<Integer> machine : plottSchedule.schedule) {
            TaskSeries series1 = new TaskSeries("Job " + jobNumber);
            Task job0 = new Task("Job 0", new SimpleTimePeriod(0, 100));


            int start = 0;
            int duration = 1;
            int startJobNumber = machine.get(0);
            boolean toPlotOrNotToPlot = false;
            System.out.println(machine);
            for (int i = 0; i < machine.size(); i++) {
                if (machine.get(i) != 0) {
                    if (machine.get(i) != startJobNumber) {
                        startJobNumber = machine.get(i);
                        //Fix boolean, plot always. Check duration
                        if (duration > 1) {
                            System.out.println("Start: " + start + " duration: " + duration);
                            job0.addSubtask(new Task("Test", new SimpleTimePeriod(start, start + duration)));
                        }
                        duration = 1;
                        start = i;

                    } else {
                        duration++;
                    }
                }
            }
            //Adds the last job
            if (duration > 1) {
                System.out.println("Start: " + start + " duration: " + duration);
                job0.addSubtask(new Task("Test", new SimpleTimePeriod(start, start + duration)));
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