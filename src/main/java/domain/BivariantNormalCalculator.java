package domain;

import java.util.function.BiFunction;

public class BivariantNormalCalculator {

    public static double computeConditionalProbability(double meanX, double meanY, double stdX, double stdY, double correlation, double a, double b) {
        // Perform numerical integration to calculate the conditional probability
        BiFunction<Double, Double, Double> bivariatePDF = (x, y) -> {
            double zX = (x - meanX) / stdX;
            double zY = (y - meanY) / stdY;
            double exponent = -1.0 / (2 * (1 - correlation * correlation)) * (
                    zX * zX - 2 * correlation * zX * zY + zY * zY
            );
            return (1 / (2 * Math.PI * stdX * stdY * Math.sqrt(1 - correlation * correlation))) * Math.exp(exponent);
        };

        double integrationResult = integrate((x, y) -> x > a && y > b ? bivariatePDF.apply(x, y) : 0, meanX - 5 * stdX, meanX + 5 * stdX, meanY - 5 * stdY, meanY + 5 * stdY);
        double normalization = integrate((x, y) -> y > b ? bivariatePDF.apply(x, y) : 0, meanX - 5 * stdX, meanX + 5 * stdX, meanY - 5 * stdY, meanY + 5 * stdY);

        return integrationResult / normalization;
    }

    public static double integrate(BiFunction<Double, Double, Double> function, double xMin, double xMax, double yMin, double yMax) {
        int steps = 100;
        double dx = (xMax - xMin) / steps;
        double dy = (yMax - yMin) / steps;
        double sum = 0.0;

        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                double x = xMin + i * dx;
                double y = yMin + j * dy;
                sum += function.apply(x, y) * dx * dy;
            }
        }

        return sum;
    }
}