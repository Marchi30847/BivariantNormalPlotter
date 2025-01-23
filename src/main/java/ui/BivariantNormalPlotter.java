package ui;

import domain.BivariantNormalCalculator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import javax.swing.*;

public class BivariantNormalPlotter {

    public static void plotFunction(double meanX, double meanY, double stdX, double stdY, double correlation) {

        XYSeries series = new XYSeries("F(x, y)");

        for (double x = -3; x <= 3; x += 0.5) {
            for (double y = -3; y <= 3; y += 0.5) {
                double probability = BivariantNormalCalculator.computeConditionalProbability(meanX, meanY, stdX, stdY, correlation, x, y);
                series.add(x, probability);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "F(x, y) = P(X > x | Y > y)",
                "x",
                "Probability",
                dataset
        );

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }
}
