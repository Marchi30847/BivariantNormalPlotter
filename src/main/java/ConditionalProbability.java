public class ConditionalProbability {
    public static double computeConditionalProbability(double a, double b, BivariateNormalDistribution dist) {
        double integrationResult = 0.0;
        double stepSize = 0.01; // Smaller step size for more accurate integration

        for (double x = a; x < a + 10; x += stepSize) {
            for (double y = b; y < b + 10; y += stepSize) {
                integrationResult += BivariateNormalPDF.computePDF(x, y, dist) * stepSize * stepSize;
            }
        }

        return integrationResult;
    }
}