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
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GanttRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.TextAnchor;

public class Visualizer extends JFrame {
    public static final TaskSeriesCollection model = new TaskSeriesCollection();

    public Visualizer(String title, Schedule plottSchedule, Double makespan) {
        super(title);

        //Create the dataset
        TaskSeriesCollection dataset = createDatasetFromSchedule(plottSchedule, makespan);

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
        range.setMaximumDate(new Date((makespan.intValue())));

        // add the chart to a panel...
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 570));
        setContentPane(chartPanel);

        //GanttRenderer personnalis..
        MyRenderer renderer = new MyRenderer(model);
        plot.setRenderer(renderer);


        //Trying to get fucking labels
        //TODO: Do some magic and make it work
        renderer.setBaseItemLabelGenerator(new IntervalCategoryItemLabelGenerator() {

            public String generateLabel(CategoryDataset dataSet, int series, int categories) {
                /* your code to get the label */
                return "Test";
            }

            public String generateColumnLabel(CategoryDataset dataset, int categories) {
                return dataset.getColumnKey(categories).toString();
            }

            public String generateRowLabel(CategoryDataset dataset, int series) {
                return dataset.getRowKey(series).toString();
            }
        });

        renderer.setBaseItemLabelsVisible(true);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.CENTER_LEFT));


        plot.setBackgroundPaint(Color.WHITE);
    }

    private static class MyRenderer extends GanttRenderer {
        private static final int PASS = 2; //Assumes two passes. Unsure what the fuck it does
        private final Color[] colorList = getUniqueColors(6);
        private final List<Color> clut = new ArrayList<>();
        private final TaskSeriesCollection model;
        private int row;
        private int col;
        private int index;

        public MyRenderer(TaskSeriesCollection model) {
            this.model = model;
        }

        @Override
        public Paint getItemPaint(int row, int col) {
            if (clut.isEmpty() || this.row != row || this.col != col) {
                fillColorList(row, col);
                this.row = row;
                this.col = col;
                index = 0;
            }
            int colorIndex = index++ / PASS;
            return clut.get(colorIndex);
        }

        //Se på tidligere. Modifiser og lag en ny liste basert på beskrivelsen til subtasken. Fyll inn den listen med de fargene så burde det funke

        private void fillColorList(int row, int col) {
            clut.clear();


            TaskSeries series = (TaskSeries) model.getRowKeys().get(row);
            List<Task> tasks = series.getTasks(); // unchecked

            int taskCount = tasks.get(col).getSubtaskCount();
            taskCount = Math.max(1, taskCount);

            //System.out.println("----> " + taskCount);
            String description;

            for (int i = 0; i < taskCount; i++) {
                description = tasks.get(col).getSubtask(i).getDescription();
                //System.out.println("Description for substask is: " + description);
                //Might need to change
                clut.add(colorList[Integer.parseInt(description) - 1]);
            }
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


    private TaskSeriesCollection createDatasetFromSchedule(Schedule plottSchedule, double makespan) {
        TaskSeriesCollection dataset = new TaskSeriesCollection();

        int machineNumber = 1;
        TaskSeries series1 = new TaskSeries("Schedule");
        //Loops over every machine in the schedule
        for (List<Integer> machine : plottSchedule.schedule) {
            Task job = new Task("Machine " + machineNumber, new SimpleTimePeriod(0, (int) makespan));


            int start = 0;
            int duration = 1;
            int startJobNumber = machine.get(0);
            for (int i = 0; i < machine.size(); i++) {
                if (machine.get(i) != 0) {
                    if (machine.get(i) != startJobNumber) {
                        //Fix boolean, plot always. Check duration
                        if (duration > 1) {
                            job.addSubtask(new Task("" + startJobNumber, new SimpleTimePeriod(start, start + duration)));
                        }
                        startJobNumber = machine.get(i);
                        duration = 1;
                        start = i;

                    } else {
                        duration++;
                    }
                }
            }
            //Adds the last job
            if (duration > 1) {
                //System.out.println("Start: " + start + " duration: " + duration);
                job.addSubtask(new Task("" + startJobNumber, new SimpleTimePeriod(start, start + duration)));
            }

            series1.add(job);

            //Task job1 = new Task("Job 1", new SimpleTimePeriod(10, 20));
            //job1.addSubtask(new Task("Test1", new SimpleTimePeriod(11, 15)));
            //series1.add(job1);

            machineNumber++;
        }
        dataset.add(series1);
        model.add(series1);
        return dataset;
    }

    private Date date(int hour) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2019, Calendar.APRIL, 1, hour, 0, 0);
        return calendar.getTime();
    }
}