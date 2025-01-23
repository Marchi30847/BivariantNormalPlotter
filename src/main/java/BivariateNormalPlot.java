import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import java.awt.*;

public class BivariateNormalPlot {

	// Method to create a 3D plot for the conditional probability P(X > a | Y > b)
	public static void plot3DConditionalProbability(double meanX, double meanY,
	                                                double sigmaX, double sigmaY,
	                                                double rho, double aMin, double aMax,
	                                                double bMin, double bMax, int resolution) {

		// Data Series to hold the 3D surface data
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries("P(X > a | Y > b)");

		// Loop through the range of a and b values
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				double a = aMin + (aMax - aMin) * i / (resolution - 1);
				double b = bMin + (bMax - bMin) * j / (resolution - 1);

				// Compute the conditional probability using the provided method
				double prob = BivariateNormalLogic.computeConditionalProbability(a, b, meanX, meanY, sigmaX, sigmaY, rho);

				// Add data to the series
				series.add(a, prob); // (a, P(X > a | Y > b))
			}
		}

		// Add series to the dataset
		dataset.addSeries(series);

		// Create the chart based on the dataset
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Conditional Probability P(X > a | Y > b)",  // Title
				"a (X-axis)",   // X-axis Label
				"P(X > a | Y > b)", // Y-axis Label
				dataset,         // Dataset
				PlotOrientation.VERTICAL,  // Plot orientation
				true,            // Include legend
				true,            // Tooltips
				false            // URLs
		);

		// Customize the plot with a renderer for better visualization
		XYPlot plot = chart.getXYPlot();
		plot.setDomainPannable(true);
		plot.setRangePannable(true);

		// Axis customization
		NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
		xAxis.setLabel("a (X-axis)");
		xAxis.setAutoRange(true);
		xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setLabel("P(X > a | Y > b)");
		yAxis.setAutoRangeIncludesZero(false);

		// Set up chart display in a JFrame
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		JFrame frame = new JFrame("3D Conditional Probability Plot");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Parameters for the bivariate normal distribution
		double meanX = 0.0, meanY = 0.0, sigmaX = 1.0, sigmaY = 1.0, rho = 0.5;
		double aMin = -3.0, aMax = 3.0, bMin = -3.0, bMax = 3.0;
		int resolution = 100;

		// Plot the conditional probability in 3D
		plot3DConditionalProbability(meanX, meanY, sigmaX, sigmaY, rho, aMin, aMax, bMin, bMax, resolution);
	}
}
