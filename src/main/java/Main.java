import domain.BivariantNormalCalculator;
import javafx.application.Application;
import ui.BivariantNormal3DPlotter;
import ui.BivariantNormalPlotter;

public class Main {
    public static void main(String[] args) {
        // Input parameters
        double meanX = 0.0;   // Mean of X
        double meanY = 0.0;   // Mean of Y
        double stdX = 1.0;    // Standard deviation of X
        double stdY = 1.0;    // Standard deviation of Y
        double correlation = 0.5; // Correlation coefficient
        double a = 1.0;       // Value for a
        double b = 1.0;       // Value for b

        // Calculate P(X > a | Y > b)
        double probability = BivariantNormalCalculator.computeConditionalProbability(meanX, meanY, stdX, stdY, correlation, a, b);
        System.out.println("P(X > " + a + " | Y > " + b + ") = " + probability);

        // Plot the function F(x, y) = P(X > x | Y > y)
        BivariantNormalPlotter.plotFunction(meanX, meanY, stdX, stdY, correlation);
    }
}