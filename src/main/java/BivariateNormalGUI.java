import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.PaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class BivariateNormalGUI {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Bivariate Normal Distribution");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 600);

			JPanel panel = new JPanel();
			panel.setLayout(new BorderLayout());

			// Input Panel
			JPanel inputPanel = new JPanel(new GridLayout(8, 2));

			JLabel labelA = new JLabel("a:");
			JTextField fieldA = new JTextField();
			JLabel labelB = new JLabel("b:");
			JTextField fieldB = new JTextField();

			JLabel labelMeanX = new JLabel("Mean X:");
			JTextField fieldMeanX = new JTextField();
			JLabel labelMeanY = new JLabel("Mean Y:");
			JTextField fieldMeanY = new JTextField();

			JLabel labelSigmaX = new JLabel("Sigma X:");
			JTextField fieldSigmaX = new JTextField();
			JLabel labelSigmaY = new JLabel("Sigma Y:");
			JTextField fieldSigmaY = new JTextField();

			JLabel labelRho = new JLabel("Correlation (rho):");
			JTextField fieldRho = new JTextField();

			JButton computeButton = new JButton("Compute and Plot");

			inputPanel.add(labelA);
			inputPanel.add(fieldA);
			inputPanel.add(labelB);
			inputPanel.add(fieldB);
			inputPanel.add(labelMeanX);
			inputPanel.add(fieldMeanX);
			inputPanel.add(labelMeanY);
			inputPanel.add(fieldMeanY);
			inputPanel.add(labelSigmaX);
			inputPanel.add(fieldSigmaX);
			inputPanel.add(labelSigmaY);
			inputPanel.add(fieldSigmaY);
			inputPanel.add(labelRho);
			inputPanel.add(fieldRho);
			inputPanel.add(computeButton);

			// Chart Panel
			JPanel chartPanel = new JPanel(new BorderLayout());

			panel.add(inputPanel, BorderLayout.NORTH);
			panel.add(chartPanel, BorderLayout.CENTER);

			frame.add(panel);
			frame.setVisible(true);


			computeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						double a = Double.parseDouble(fieldA.getText());
						double b = Double.parseDouble(fieldB.getText());
						double meanX = Double.parseDouble(fieldMeanX.getText());
						double meanY = Double.parseDouble(fieldMeanY.getText());
						double sigmaX = Double.parseDouble(fieldSigmaX.getText());
						double sigmaY = Double.parseDouble(fieldSigmaY.getText());
						double rho = Double.parseDouble(fieldRho.getText());

						// Compute the probability F(a, b)
						double result = BivariateNormalLogic.computeConditionalProbability(a, b, meanX, meanY, sigmaX, sigmaY, rho);
						JOptionPane.showMessageDialog(frame, "P(X > " + a + " | Y > " + b + ") = " + result);

						// Create the scatter plot dataset
						XYSeries series = new XYSeries("F(x, y)");
						for (double x = a; x <= a + 3; x += 0.1) {
							for (double y = b; y <= b + 3; y += 0.1) {
								double probability = BivariateNormalLogic.computeConditionalProbability(x, y, meanX, meanY, sigmaX, sigmaY, rho);
								series.add(x, probability);
							}
						}
						XYSeriesCollection scatterDataset = new XYSeriesCollection(series);
						JFreeChart scatterChart = ChartFactory.createScatterPlot(
								"Bivariate Normal Distribution",
								"X", "P(X > x | Y > y)",
								scatterDataset
						);

						// Create the heatmap dataset
						double[][] data = new double[100][100];
						double xStart = -5, xEnd = 5, yStart = -5, yEnd = 5;
						double dx = (xEnd - xStart) / 99;
						double dy = (yEnd - yStart) / 99;

						for (int i = 0; i < 100; i++) {
							for (int j = 0; j < 100; j++) {
								double x = xStart + i * dx;
								double y = yStart + j * dy;
								data[i][j] = BivariateNormalLogic.computeConditionalProbability(x, y, meanX, meanY, sigmaX, sigmaY, rho);
							}
						}
						JFreeChart heatmap = createHeatmapChart(data, "Heatmap");

						// Display scatter plot and heatmap side by side
						ChartPanel scatterPanel = new ChartPanel(scatterChart);
						ChartPanel heatmapPanel = new ChartPanel(heatmap);

						chartPanel.removeAll();
						chartPanel.setLayout(new GridLayout(1, 2)); // Scatter plot on the left, heatmap on the right
						chartPanel.add(scatterPanel);
						chartPanel.add(heatmapPanel);
						chartPanel.validate();

					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(frame, "Please enter valid numerical inputs.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});

		});


	}

	private static JFreeChart createHeatmapChart(double[][] data, String title) {
		int xCount = data.length;
		int yCount = data[0].length;
		double xStart = -5, xEnd = 5, yStart = -5, yEnd = 5;

		DefaultXYZDataset dataset = new DefaultXYZDataset();
		double[] xValues = new double[xCount * yCount];
		double[] yValues = new double[xCount * yCount];
		double[] zValues = new double[xCount * yCount];

		int index = 0;
		double dx = (xEnd - xStart) / (xCount - 1);
		double dy = (yEnd - yStart) / (yCount - 1);

		for (int i = 0; i < xCount; i++) {
			for (int j = 0; j < yCount; j++) {
				xValues[index] = xStart + i * dx;
				yValues[index] = yStart + j * dy;
				zValues[index] = data[i][j];
				index++;
			}
		}
		dataset.addSeries("Probability", new double[][]{xValues, yValues, zValues});

		NumberAxis xAxis = new NumberAxis("X");
		NumberAxis yAxis = new NumberAxis("Y");
		xAxis.setAutoRangeIncludesZero(false);
		yAxis.setAutoRangeIncludesZero(false);

		XYBlockRenderer renderer = new XYBlockRenderer();
		PaintScale scale = new LookupPaintScale(0, 1, Color.BLACK);
		for (int i = 0; i <= 255; i++) {
			float value = i / 255f;
			((LookupPaintScale) scale).add(value, new Color(value, value, 1 - value));
		}
		renderer.setPaintScale(scale);

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);

		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);

		// Add a color bar (legend) to the chart
		PaintScaleLegend legend = new PaintScaleLegend(scale, new NumberAxis("P(X > x | Y > y)"));
		legend.setPosition(RectangleEdge.RIGHT);
		chart.addSubtitle(legend);

		return chart;
	}
}
