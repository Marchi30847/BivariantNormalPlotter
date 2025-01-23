import numpy as np
import matplotlib.pyplot as plt
import sys
import math

if len(sys.argv) != 8:
    print("Usage: [x_mean] [x_stddev] [y_mean] [y_stddev] [corr] [a] [b]")
    sys.exit(1)

x_mean = float(sys.argv[1])
x_stddev = float(sys.argv[2])
y_mean = float(sys.argv[3])
y_stddev = float(sys.argv[4])
corr = float(sys.argv[5])
a = float(sys.argv[6])
b = float(sys.argv[7])

def pdfBivariateNormalDist(x, y):
    """
    Returns the value of the PDF (Probability Density Function) for the
    Bivariate Normal (Gaussian) distribution at the point (x, y) for the given:

    x_mean: Mean of X
    x_stddev: Standard deviation of X
    y_mean: Mean of Y
    y_stddev: Standard deviation of Y
    corr: Correlation coefficient between X and Y (between -1 and 1)

    Formula reference:
    f(x, y) = 1 / (2 * pi * sigma_x * sigma_y * sqrt(1 - rho^2))
              * exp( -1/(2 * (1 - rho^2)) * [ (x - mu_x)^2 / sigma_x^2
                                            + (y - mu_y)^2 / sigma_y^2
                                            - 2 * rho * (x - mu_x)(y - mu_y)
                                              / (sigma_x * sigma_y) ] )
    """
    # Check the correlation is in valid range
    if not -1 <= corr <= 1:
        raise ValueError("Correlation coefficient must be between -1 and 1.")
    
    # Precompute constants
    two_pi = 2 * math.pi
    rho_squared = corr ** 2
    denom = 2 * (1 - rho_squared)

    # Normalizing constant for the bivariate distribution
    normalization = 1.0 / (two_pi * x_stddev * y_stddev * math.sqrt(1 - rho_squared))

    # Compute the exponent
    x_term = (x - x_mean) / x_stddev
    y_term = (y - y_mean) / y_stddev
    exponent = -1.0 / denom * (x_term**2 + y_term**2 - 2 * corr * x_term * y_term)

    # Return the PDF value
    return normalization * math.exp(exponent)

# Generate x and y values
x = np.linspace(-5, 5, 100)
y = np.linspace(-5, 5, 100)
x, y = np.meshgrid(x, y)  # Create a 2D grid

# Compute z values
vectorized_pdf = np.vectorize(pdfBivariateNormalDist)
z = vectorized_pdf(x, y)

# Create the color plot
plt.figure(figsize=(8, 6))
contour = plt.contourf(x, y, z, levels=50, cmap='viridis')  # Filled contours

# Add a color bar
plt.colorbar(contour, label='pdfBivariateNormalDist(x, y)')

# Add labels and title
plt.title("Plot of bivariate normal distribution")
plt.xlabel("X")
plt.ylabel("Y")

# Show the plot
plt.show()
