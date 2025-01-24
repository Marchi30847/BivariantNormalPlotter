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
a_input = float(sys.argv[6])
b_input = float(sys.argv[7])

def pdfBivariateNormalDist(x, y):
    """
    Returns the value of the PDF (Probability Density Function) for the
    Bivariate Normal distribution at the point (x, y) for the given:

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

	Source: https://webspace.maths.qmul.ac.uk/a.gnedin/LNotesStats/MS_Lectures_5.pdf
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

def P_X_greater_a_Y_greater_b(a, b, steps=200, range_factor=5.0):
    """
    Numerically approximates P(X > a, Y > b) by summing over a grid.
    We integrate over x in [a, x_max], y in [b, y_max], where
    x_max = x_mean + range_factor * x_stddev,
    y_max = y_mean + range_factor * y_stddev.

    The 'steps' parameter controls the resolution of the grid.
    """
    # Define the upper integration limits (finite approximation)
    x_max = x_mean + range_factor * x_stddev
    y_max = y_mean + range_factor * y_stddev

    # If a is greater than x_max (or b is greater than y_max), prob is effectively 0
    if a >= x_max or b >= y_max:
        return 0.0

    # Create linearly spaced grids for x and y
    x_grid = np.linspace(a, x_max, steps)
    y_grid = np.linspace(b, y_max, steps)

    dx = (x_max - a) / (steps - 1)
    dy = (y_max - b) / (steps - 1)

    # Double sum to approximate the integral
    total = 0.0
    for i in range(steps):
        for j in range(steps):
            x_val = x_grid[i]
            y_val = y_grid[j]
            total += pdfBivariateNormalDist(x_val, y_val) * dx * dy

    if total >= 1.0:
        return 1.0

    return total

def P_Y_greater_b(b, steps=200, range_factor=5.0):
    """
    Numerically approximates P(Y > b) by summing over a grid.
    We integrate over x in [x_min, x_max], y in [b, y_max], where
    x_min = x_mean - range_factor * x_stddev,
    x_max = x_mean + range_factor * x_stddev,
    y_max = y_mean + range_factor * y_stddev.
    """
    # Define the integration limits (finite approximation)
    x_min = x_mean - range_factor * x_stddev
    x_max = x_mean + range_factor * x_stddev
    y_max = y_mean + range_factor * y_stddev

    # If b >= y_max, prob is effectively 0
    if b >= y_max:
        return 0.0

    # Create linearly spaced grids for x and y
    x_grid = np.linspace(x_min, x_max, steps)
    y_grid = np.linspace(b, y_max, steps)

    dx = (x_max - x_min) / (steps - 1)
    dy = (y_max - b) / (steps - 1)

    total = 0.0
    for i in range(steps):
        for j in range(steps):
            x_val = x_grid[i]
            y_val = y_grid[j]
            total += pdfBivariateNormalDist(x_val, y_val) * dx * dy

    if total >= 1.0:
        return 1.0

    return total

def conditional_P_X_greater_a_given_Y_greater_b(a, b, steps=50, range_factor=5.0):
    """
    Calculates P(X > a | Y > b) = P(X > a, Y > b) / P(Y > b)
    using simple numerical approximation with two nested loops.
    """
    numerator = P_X_greater_a_Y_greater_b(a, b, steps, range_factor)
    denominator = P_Y_greater_b(b, steps, range_factor)

    if denominator == 0:
        return float('nan')
    value = numerator / denominator
    if value >= 1.0:
        return 1.0
    return value

prob = conditional_P_X_greater_a_given_Y_greater_b(a_input, b_input)
print("P( X > a | Y > b ) = P( X >", a_input, "| Y >", b_input, ") =", prob)

# Generate x and y values
x = np.linspace(x_mean-5, x_mean+5, 20)
y = np.linspace(y_mean-5, y_mean+5, 20)
x, y = np.meshgrid(x, y)  # Create a 2D grid

# Compute z values
vectorized_function = np.vectorize(conditional_P_X_greater_a_given_Y_greater_b)
#vectorized_function = np.vectorize(pdfBivariateNormalDist)
z = vectorized_function(x, y)
#z = conditional_P_X_greater_a_given_Y_greater_b(x, y)

# Create the color plot
plt.figure(figsize=(8, 6))
contour = plt.contourf(x, y, z, levels=50, cmap='viridis', vmin=0.0, vmax=1.0)  # Filled contours

# Add a color bar
plt.colorbar(contour, label='P( X > x | Y > y )')

# Add labels and title
plt.title("Plot of P( X > x | Y > y )")
plt.xlabel("X")
plt.ylabel("Y")

# Show the plot
plt.show()
