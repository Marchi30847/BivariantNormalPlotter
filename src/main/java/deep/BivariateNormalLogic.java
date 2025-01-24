package deep;

public class BivariateNormalLogic {

	// Compute conditional probability P(X > a | Y > b)
	public static double computeConditionalProbability(double a, double b,
	                                                   double meanX, double meanY,
	                                                   double sigmaX, double sigmaY,
	                                                   double rho) {
		double jointProbability = computeJointProbability(a, b, meanX, meanY, sigmaX, sigmaY, rho);
		double marginalProbabilityY = computeMarginalProbabilityY(b, meanY, sigmaY);

		// Handle division by near-zero marginal probabilities
		if (marginalProbabilityY < 1e-10 || Double.isNaN(jointProbability) || Double.isNaN(marginalProbabilityY)) {
			return 0.0;
		}

		return jointProbability / marginalProbabilityY;
	}


	// Compute joint probability P(X > a, Y > b)
	public static double computeJointProbability(double a, double b,
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

	// Compute the marginal probability for Y > b
	public static double computeMarginalProbabilityY(double b, double mean, double sigma) {
		double z = (b - mean) / sigma;
		return 1 - cumulativeStandardNormal(z);
	}

	// Bivariate Normal PDF
	public static double bivariateNormalPDF(double x, double y, double meanX, double meanY, double sigmaX, double sigmaY, double rho) {
		double z = ((x - meanX) / sigmaX) * ((x - meanX) / sigmaX)
				- 2 * rho * ((x - meanX) / sigmaX) * ((y - meanY) / sigmaY)
				+ ((y - meanY) / sigmaY) * ((y - meanY) / sigmaY);
		return Math.exp(-z / (2 * (1 - rho * rho)))
				/ (2 * Math.PI * sigmaX * sigmaY * Math.sqrt(1 - rho * rho));
	}

	// Cumulative Standard Normal Distribution
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

	// Standard Normal PDF
	public static double standardNormalPDF(double x) {
		return Math.exp(-0.5 * x * x) / Math.sqrt(2 * Math.PI);
	}
}
