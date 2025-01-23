import java.util.Scanner;

public class Main {
    public static double standardNormalPDF(double z) {
        return Math.exp(-0.5 * z * z) / Math.sqrt(2.0 * Math.PI);
    }

    public static double standardNormalCDF(double z) {
        final double b1 =  0.319381530;
        final double b2 = -0.356563782;
        final double b3 =  1.781477937;
        final double b4 = -1.821255978;
        final double b5 =  1.330274429;
        final double p  =  0.2316419;

        double t = 1.0 / (1.0 + p * Math.abs(z));
        double poly = b1*t + b2*t*t + b3*t*t*t + b4*t*t*t*t + b5*t*t*t*t*t;
        double approx = 1.0 - standardNormalPDF(z) * poly;
        return (z >= 0.0) ? approx : 1.0 - approx;
    }

    public static double bivariateNormalPDF(double x, double y,
                                            double muX, double muY,
                                            double sigmaX, double sigmaY,
                                            double rho)
    {
        double norm = 1.0 / (2.0 * Math.PI * sigmaX * sigmaY * Math.sqrt(1 - rho*rho));
        double dx = (x - muX) / sigmaX;
        double dy = (y - muY) / sigmaY;

        double z = dx*dx - 2*rho*dx*dy + dy*dy;
        z = z / (2.0*(1 - rho*rho));

        return norm * Math.exp(-z);
    }

    public static double pXGreaterThanAndYGreaterThan(double x, double y,
                                                      double muX, double muY,
                                                      double sigmaX, double sigmaY,
                                                      double rho)
    {
        double xMax = muX + 5.0 * sigmaX;
        double yMax = muY + 5.0 * sigmaY;

        if (xMax < x) {
            return 0.0;
        }

        if (yMax < y) {
            return 0.0;
        }

        int steps = 200;
        double dx = (xMax - x) / steps;
        double dy = (yMax - y) / steps;

        double sum = 0.0;

        for (int i = 0; i < steps; i++) {
            double curY = y + (i + 0.5)*dy;
            for (int j = 0; j < steps; j++) {
                double curX = x + (j + 0.5)*dx;
                double pdfVal = bivariateNormalPDF(curX, curY,
                        muX, muY,
                        sigmaX, sigmaY,
                        rho);
                sum += pdfVal;
            }
        }
        sum *= (dx * dy);

        return sum;
    }

    public static double pYGreaterThan(double y, double muY, double sigmaY) {
        double z = (y - muY)/sigmaY;
        double cdf = standardNormalCDF(z);
        return 1.0 - cdf;
    }

    public static double Fxy(double x, double y,
                             double muX, double muY,
                             double sigmaX, double sigmaY,
                             double rho)
    {
        double numerator = pXGreaterThanAndYGreaterThan(x, y, muX, muY, sigmaX, sigmaY, rho);
        double denominator = pYGreaterThan(y, muY, sigmaY);

        if (denominator < 1e-14) {
            return 0.0;
        }

        return numerator / denominator;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Enter muX, muY, sigmaX, sigmaY, rho:");
        double muX = in.nextDouble();
        double muY = in.nextDouble();
        double sigmaX = in.nextDouble();
        double sigmaY = in.nextDouble();
        double rho   = in.nextDouble();

        System.out.println("Enter a, b:");
        double a = in.nextDouble();
        double b = in.nextDouble();

        double xMin = muX - 2*sigmaX;
        double xMax = muX + 2*sigmaX;
        double yMin = muY - 2*sigmaY;
        double yMax = muY + 2*sigmaY;

        int steps = 10;
        double dx = (xMax - xMin)/(steps - 1);
        double dy = (yMax - yMin)/(steps - 1);

        System.out.println("\n--- Plot of F(x,y) over a grid ---");
        for (int i = 0; i < steps; i++) {
            double yy = yMin + i*dy;
            for (int j = 0; j < steps; j++) {
                double xx = xMin + j*dx;
                double val = Fxy(xx, yy, muX, muY, sigmaX, sigmaY, rho);
                System.out.printf("%.4f ", val);
            }
            System.out.println();
        }

        double finalVal = Fxy(a, b, muX, muY, sigmaX, sigmaY, rho);
        System.out.printf("%nF(a,b) = P(X>%.2f | Y>%.2f) = %.6f%n", a, b, finalVal);
    }
}