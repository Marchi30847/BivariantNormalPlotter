public class BivariateNormalPDF {
    public static double computePDF(double x, double y, BivariateNormalDistribution dist) {
        double covXY = dist.correlation * dist.stdDevX * dist.stdDevY;
        double varX = Math.pow(dist.stdDevX, 2);
        double varY = Math.pow(dist.stdDevY, 2);

        double denominator = 2 * Math.PI * dist.stdDevX * dist.stdDevY * Math.sqrt(1 - Math.pow(dist.correlation, 2));
        double exponent = -1.0 / (2 * (1 - Math.pow(dist.correlation, 2))) * (
                (Math.pow(x - dist.meanX, 2) / varX) +
                        (Math.pow(y - dist.meanY, 2) / varY) -
                        (2 * dist.correlation * (x - dist.meanX) * (y - dist.meanY) / (dist.stdDevX * dist.stdDevY))
        );

        return (1.0 / denominator) * Math.exp(exponent);
    }
}