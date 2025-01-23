import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;

public class BivariateNormalPlot {

	public static void main(String[] args) {
		// Input parameters
		double meanX = 0, meanY = 0, sigmaX = 1, sigmaY = 1, rho = 0.5;
		double a = 0.5, b = 0.5;

		// Compute the conditional probability for X > a | Y > b
		double conditionalProbability = BivariateNormalLogic.computeConditionalProbability(a, b, meanX, meanY, sigmaX, sigmaY, rho);
		System.out.printf("P(X > %.2f | Y > %.2f) = %.5f%n", a, b, conditionalProbability);

		// Launch the graph plotting in Swing
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Bivariate Conditional Probability Plot");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(createChartPanel(meanX, meanY, sigmaX, sigmaY, rho));
			frame.pack();
			frame.setVisible(true);
		});
	}

	// Create chart panel
	public static JPanel createChartPanel(double meanX, double meanY, double sigmaX, double sigmaY, double rho) {
		// Create a series for the graph
		XYSeries series = new XYSeries("F(x, y) with fixed y=0.5");
		double y = 0.5;

		// Compute the conditional probability for X in the range [-3, 3]
		for (double x = -3.0; x <= 3.0; x += 0.1) {
			double probability = BivariateNormalLogic.computeConditionalProbability(x, y, meanX, meanY, sigmaX, sigmaY, rho);
			series.add(x, probability);
		}

		// Create a dataset from the series
		XYSeriesCollection dataset = new XYSeriesCollection(series);

		// Create the chart
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Bivariate Conditional Probability",  // Title
				"X",                                // X-axis Label
				"F(X, Y)",                          // Y-axis Label
				dataset,                            // Dataset
				PlotOrientation.VERTICAL,           // Plot orientation
				true,                               // Include legend
				true,                               // Tooltips
				false                               // URLs
		);

		// Return the chart panel for displaying
		return new ChartPanel(chart);
	}
}
