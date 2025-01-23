import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.DefaultXYZDataset;

import javax.swing.*;

public class GraphPlotter extends JPanel {
    BivariateNormalDistribution dist;
    double a, b;

    public GraphPlotter(BivariateNormalDistribution dist, double a, double b) {
        this.dist = dist;
        this.a = a;
        this.b = b;
        createChart();
    }

    private void createChart() {
        DefaultXYZDataset dataset = new DefaultXYZDataset();

        // Create data for the chart
        int numPoints = 100;
        double[][] data = new double[3][numPoints * numPoints];
        double stepSize = 10.0 / numPoints;

        int index = 0;
        for (int i = 0; i < numPoints; i++) {
            System.out.println("i = " + i);
            System.out.println(data[0][index]);
            System.out.println(data[1][index]);
            System.out.println(data[2][index]);
            for (int j = 0; j < numPoints; j++) {
                double x = i * stepSize;
                double y = j * stepSize;
                data[0][index] = x; // X values
                data[1][index] = y; // Y values
                data[2][index] = ConditionalProbability.computeConditionalProbability(x, y, dist); // Z values
                index++;
            }
        }

        dataset.addSeries("Probability", data);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Bivariate Normal Distribution",
                "X",
                "Y",
                dataset
        );

        XYPlot plot = (XYPlot) chart.getPlot();
        XYBlockRenderer renderer = new XYBlockRenderer();
        plot.setRenderer(renderer);

        ValueAxis domainAxis = new NumberAxis("X");
        ValueAxis rangeAxis = new NumberAxis("Y");
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 800));
        this.add(chartPanel);

        // Output probability F(a, b)
        double probability = ConditionalProbability.computeConditionalProbability(a, b, dist);
        System.out.println("F(a, b) = " + probability);
    }
}