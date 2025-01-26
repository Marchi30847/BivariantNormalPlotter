import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import sys
import math

def incorrect_arguments():
    print("Usage: (x_mean) (x_stddev) (y_mean) (y_stddev) (corr) (a) (b) [range_factor] [steps]")
    print("  [range_factor] - optional - defines how far away to integrate and draw the graph from the x and y means.")
    print("    By default it is set to 5 * x_stddev or 5 * y_stddev, whichever is larger.")
    print("  [steps] - optional - number of steps used for numerical integration. The default value is 3000.")
    sys.exit(1)

if len(sys.argv) < 8 or len(sys.argv) > 10:
    incorrect_arguments()

x_mean = float(sys.argv[1])
x_stddev = float(sys.argv[2])
y_mean = float(sys.argv[3])
y_stddev = float(sys.argv[4])
corr = float(sys.argv[5])
a_input = float(sys.argv[6])
b_input = float(sys.argv[7])

range_factor = max(5.0*x_stddev, 5.0*y_stddev)
steps = 3000

if len(sys.argv) > 8:
    range_factor = int(sys.argv[8])

if len(sys.argv) > 9:
    steps = int(sys.argv[9])

if x_stddev <= 0 or y_stddev <= 0:
    print("The x and y standard deviations cannot be negative or zero.")
    incorrect_arguments()

if not -1 < corr < 1:
    print("Correlation coefficient must be between (-1, 1) - not including {-1, 1}.")
    incorrect_arguments()

def pdf_bivariate_normal_dist(x, y):
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
    two_pi = 2 * math.pi
    rho_squared = corr ** 2
    denom = 2 * (1 - rho_squared)

    normalization = 1.0 / (two_pi * x_stddev * y_stddev * math.sqrt(1 - rho_squared))

    x_term = (x - x_mean) / x_stddev
    y_term = (y - y_mean) / y_stddev
    exponent = -1.0 / denom * (x_term**2 + y_term**2 - 2 * corr * x_term * y_term)

    return normalization * math.exp(exponent)

# Define integration limits
x_min = x_mean - range_factor
x_max = x_mean + range_factor
y_min = y_mean - range_factor
y_max = y_mean + range_factor

# Create a dense grid
x_values = np.linspace(x_min, x_max, steps)
y_values = np.linspace(y_min, y_max, steps)
dx = x_values[1] - x_values[0]
dy = y_values[1] - y_values[0]

# Create meshgrid
X, Y = np.meshgrid(x_values, y_values)

# Compute the PDF over the grid
vectorized_pdf = np.vectorize(pdf_bivariate_normal_dist)
Z = vectorized_pdf(X, Y)

# Compute P(Y > y) as a 1D array
# Sum over x for each y, then cumulative sum over y in descending order
sum_over_x = np.sum(Z, axis=1)  # Shape: (steps,)
cumsum_rev_y = np.cumsum(sum_over_x[::-1]) * dx  # Cumulative sum from y_max downward
P_Y_gt = cumsum_rev_y[::-1] * dy  # Reverse back to original order

# Compute P(X > x, Y > y) as a 2D array
# Perform 2D cumulative sum from bottom-right to top-left
Z_rev = Z[::-1, ::-1]
cumsum_x_rev = np.cumsum(np.cumsum(Z_rev, axis=0), axis=1) * dx * dy
P_X_gt_X_Y_gt_Y = cumsum_x_rev[::-1, ::-1]

# Ensure probabilities do not exceed 1 due to numerical errors
P_Y_gt = np.minimum(P_Y_gt, 1.0)
P_X_gt_X_Y_gt_Y = np.minimum(P_X_gt_X_Y_gt_Y, 1.0)

# Compute the conditional probability P(X > x | Y > y)
conditional_prob = np.where(P_Y_gt[:, np.newaxis] > 0,
                            P_X_gt_X_Y_gt_Y / P_Y_gt[:, np.newaxis],
                            np.nan)
conditional_prob = np.minimum(conditional_prob, 1.0)  # Cap at 1.0

def get_conditional_prob(a, b):
    """
    Retrieves the conditional probability P(X > a | Y > b) using precomputed arrays.
    """
    # Find the index for a and b
    ix = np.searchsorted(x_values, a, side='left')
    iy = np.searchsorted(y_values, b, side='left')

    if ix >= steps or iy >= steps:
        # P(Y > y) = 0.0 => probability couldn't be calculated
        return float('nan')

    # Ensure indices are within bounds
    ix = min(ix, steps - 1)
    iy = min(iy, steps - 1)

    prob = conditional_prob[iy, ix]
    return prob

# Compute the probability for the input a and b
prob = get_conditional_prob(a_input, b_input)
print(f"P(X > {a_input} | Y > {b_input}) = {prob}")

# Generate x and y values for plotting
plot_steps = 100
plot_x = np.linspace(x_min, x_max, plot_steps)
plot_y = np.linspace(y_min, y_max, plot_steps)
plot_X, plot_Y = np.meshgrid(plot_x, plot_y)

# Vectorize the get_conditional_prob function for plotting
vectorized_get_conditional_prob = np.vectorize(get_conditional_prob)
plot_Z = vectorized_get_conditional_prob(plot_X, plot_Y)

# Create a 3D plot
fig = plt.figure(figsize=(10, 8))
ax = fig.add_subplot(111, projection='3d')

# Plot the surface
surface = ax.plot_surface(plot_X, plot_Y, plot_Z, cmap='viridis', edgecolor='k', linewidth=0.5, antialiased=True)

# Add a color bar
fig.colorbar(surface, ax=ax, shrink=0.5, aspect=10, label='F(x, y) = P(X > x | Y > y)')

# Add labels and title
ax.set_title("Plot of F(x, y) = P(X > x | Y > y)")
ax.set_xlabel("X")
ax.set_ylabel("Y")
ax.set_zlabel("Probability")

# Show the plot
plt.show()
