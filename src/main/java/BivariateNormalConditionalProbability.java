import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.*;

public class BivariateNormalConditionalProbability {

	public static void main(String[] args) {
		// Input parameters
		double meanX = 0, meanY = 0, sigmaX = 1, sigmaY = 1, rho = 0.5;
		double a = 0.5, b = 0.5;

		double conditionalProbability = computeConditionalProbability(a, b, meanX, meanY, sigmaX, sigmaY, rho);
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

	public static JPanel createChartPanel(double meanX, double meanY, double sigmaX, double sigmaY, double rho) {
		// Create a series for the graph
		XYSeries series = new XYSeries("F(x, y) with fixed y=0.5");
		double y = 0.5;

		for (double x = -3.0; x <= 3.0; x += 0.1) {
			double probability = computeConditionalProbability(x, y, meanX, meanY, sigmaX, sigmaY, rho);
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

		// Create a panel to display the chart
		return new ChartPanel(chart);
	}

	public static double computeConditionalProbability(double a, double b,
	                                                   double meanX, double meanY,
	                                                   double sigmaX, double sigmaY,
	                                                   double rho) {
		double jointProbability = computeJointProbability(a, b, meanX, meanY, sigmaX, sigmaY, rho);
		double marginalProbabilityY = computeMarginalProbabilityY(b, meanY, sigmaY);
		return marginalProbabilityY < 1e-10 ? 0.0 : jointProbability / marginalProbabilityY;
	}

	private static double computeJointProbability(double a, double b,
	                                              double meanX, double meanY,
	                                              double sigmaX, double sigmaY,
	                                              double rho) {
		int stepsX = 100, stepsY = 100;
		double lowerX = a, upperX = 6, lowerY = b, upperY = 6;
		double dx = (upperX - lowerX) / stepsX;
		double dy = (upperY - lowerY) / stepsY;

		double sum = 0.0;
		for (int i = 0; i <= stepsX; i++) {
			for (int j = 0; j <= stepsY; j++) {
				double x = lowerX + i * dx;
				double y = lowerY + j * dy;
				double weightX = (i == 0 || i == stepsX) ? 1 : (i % 2 == 0 ? 2 : 4);
				double weightY = (j == 0 || j == stepsY) ? 1 : (j % 2 == 0 ? 2 : 4);
				sum += weightX * weightY * bivariateNormalPDF(x, y, meanX, meanY, sigmaX, sigmaY, rho);
			}
		}
		return sum * dx * dy / 9.0;
	}

	private static double computeMarginalProbabilityY(double b, double mean, double sigma) {
		double z = (b - mean) / sigma;
		return 1 - cumulativeStandardNormal(z);
	}

	private static double bivariateNormalPDF(double x, double y, double meanX, double meanY, double sigmaX, double sigmaY, double rho) {
		double z = ((x - meanX) / sigmaX) * ((x - meanX) / sigmaX)
				- 2 * rho * ((x - meanX) / sigmaX) * ((y - meanY) / sigmaY)
				+ ((y - meanY) / sigmaY) * ((y - meanY) / sigmaY);
		return Math.exp(-z / (2 * (1 - rho * rho)))
				/ (2 * Math.PI * sigmaX * sigmaY * Math.sqrt(1 - rho * rho));
	}

	public static double cumulativeStandardNormal(double z) {
		if (z < -6.0) return 0.0;
		if (z > 6.0) return 1.0;

		int steps = 10000;
		double stepSize = z / steps;
		double sum = 0.0;

		for (int i = 0; i <= steps; i++) {
			double x = i * stepSize;
			double weight = (i == 0 || i == steps) ? 1 : (i % 2 == 0 ? 2 : 4);
			sum += weight * standardNormalPDF(x);
		}
		return (sum * stepSize / 3.0) + 0.5;
	}

	public static double standardNormalPDF(double x) {
		return Math.exp(-0.5 * x * x) / Math.sqrt(2 * Math.PI);
	}
}
