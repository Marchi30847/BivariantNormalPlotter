class BivariateNormalDistribution {
    double meanX, meanY, stdDevX, stdDevY, correlation;

    public BivariateNormalDistribution(double meanX, double meanY, double stdDevX, double stdDevY, double correlation) {
        this.meanX = meanX;
        this.meanY = meanY;
        this.stdDevX = stdDevX;
        this.stdDevY = stdDevY;
        this.correlation = correlation;
    }
}