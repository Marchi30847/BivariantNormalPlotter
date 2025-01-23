import java.util.Scanner;

public class BivariateNormalConditionalProbability {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Input for distribution parameters
		System.out.print("Enter mean of X: ");
		double meanX = scanner.nextDouble();
		System.out.print("Enter mean of Y: ");
		double meanY = scanner.nextDouble();
		System.out.print("Enter standard deviation of X: ");
		double sigmaX = scanner.nextDouble();
		System.out.print("Enter standard deviation of Y: ");
		double sigmaY = scanner.nextDouble();
		System.out.print("Enter correlation coefficient: ");
		double rho = scanner.nextDouble();

		// Input for probability thresholds
		System.out.print("Enter threshold value for X (a): ");
		double a = scanner.nextDouble();
		System.out.print("Enter threshold value for Y (b): ");
		double b = scanner.nextDouble();

		double result = computeConditionalProbability(meanX, meanY, sigmaX, sigmaY, rho, a, b);

		System.out.printf("P(X > %.2f | Y > %.2f) = %.4f%n", a, b, result);

		scanner.close();
	}

	// Compute P(X > a | Y > b) using numerical integration
	private static double computeConditionalProbability(double meanX, double meanY, double sigmaX, double sigmaY, double rho, double a, double b) {
		double integralX = 0.0;
		double integralY = 0.0;
		double stepSize = 0.01;
		double upperBound = 5;  // Assuming most of the probability mass is within +/- 5 standard deviations

		for (double y = b; y < upperBound; y += stepSize) {
			for (double x = a; x < upperBound; x += stepSize) {
				double pdf = pdfBivariateNormal(x, y, meanX, meanY, sigmaX, sigmaY, rho);
				integralX += pdf * stepSize;
			}
			double pdfY = pdfNormal((y - meanY) / sigmaY);
			integralY += pdfY * stepSize;
		}

		return integralY > 0 ? (integralX / integralY) : 0;
	}

	// Probability Density Function for bivariate normal distribution
	private static double pdfBivariateNormal(double x, double y, double muX, double muY, double sigmaX, double sigmaY, double rho) {
		double z = ((x - muX) * (x - muX)) / (sigmaX * sigmaX) -
				(2 * rho * (x - muX) * (y - muY)) / (sigmaX * sigmaY) +
				((y - muY) * (y - muY)) / (sigmaY * sigmaY);
		double norm = 1 / (2 * Math.PI * sigmaX * sigmaY * Math.sqrt(1 - rho * rho));
		return norm * Math.exp(-z / (2 * (1 - rho * rho)));
	}

	// Probability Density Function for normal distribution
	private static double pdfNormal(double z) {
		return (1 / Math.sqrt(2 * Math.PI)) * Math.exp(-0.5 * z * z);
	}
}