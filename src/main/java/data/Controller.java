package data;

public class Controller {
    private final double meanA;
    private final double meanB;
    private final double stdDevA;
    private final double stdDevB;
    private final double correlation;

    // Additional values for creating the bivariant normal distribution
    private double covariance;
    private double determinant;
    private double normalisationIndex;

    private double density = 0.0;

    public Controller(double meanA, double meanB, double stdDevA, double stdDevB, double correlation) {
        this.meanA = meanA;
        this.meanB = meanB;
        this.stdDevA = stdDevA;
        this.stdDevB = stdDevB;
        this.correlation = correlation;
    }

    public Controller calculateGraphVariables() {
        covariance = correlation * stdDevA * stdDevB;
        determinant = Math.sqrt(Math.pow(stdDevA, 2) * Math.pow(stdDevB, 2) * (Math.pow(covariance, 2)));
        normalisationIndex = 1.0 / (2 * Math.PI * determinant);

        return this;
    }

    public Controller calculateDensity(double x, double y) {
        double zX = (x - meanA) / stdDevA;
        double zY = (y - meanB) / stdDevB;

        double exponent = -0.5 * ((zX * zX) - 2 * correlation * zX * zY + (zY * zY)) / (1 - correlation * correlation);
        density = normalisationIndex * Math.exp(exponent);

        return this;
    }

    public double getDensity() {
        return density;
    }

    public double getMeanA() {
        return meanA;
    }

    public double getMeanB() {
        return meanB;
    }

    public double getStdDevA() {
        return stdDevA;
    }

    public double getStdDevB() {
        return stdDevB;
    }

    public double getCorrelation() {
        return correlation;
    }
}
