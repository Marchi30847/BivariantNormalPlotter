import numpy as np
import matplotlib.pyplot as plt
import sys

if len(sys.argv) != 8:
	print("Usage: [x_mean] [x_stddev] [y_mean] [y_stddev] [corr] [a] [b]")
	sys.exit(1)

x_mean = sys.argv[1];
x_stddev = sys.argv[2];
y_mean = sys.argv[3];
y_stddev = sys.argv[4];
corr = sys.argv[5];
a = sys.argv[6];
b = sys.argv[7];

def pdfBivariateNormalDist(x, y):
	# Dummy function
    return x+y

# Generate x and y values
x = np.linspace(-5, 5, 100)
y = np.linspace(-5, 5, 100)
x, y = np.meshgrid(x, y)  # Create a 2D grid

# Compute z values
z = pdfBivariateNormalDist(x, y)

# Create the color plot
plt.figure(figsize=(8, 6))
contour = plt.contourf(x, y, z, levels=50, cmap='viridis')  # Filled contours

# Add a color bar
plt.colorbar(contour, label='f(x, y)')

# Add labels and title
plt.title("Plot of bivariate normal distribution")
plt.xlabel("X")
plt.ylabel("Y")

# Show the plot
plt.show()
