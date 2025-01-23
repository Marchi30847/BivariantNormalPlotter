package ui.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GraphPanel extends JPanel {
    private final GraphPanelCallback callback;

    public GraphPanel(GraphPanelCallback callback) {
        super();

        this.callback = callback;

        configure();
    }

    private void configure() {
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        // Parameters of the bivariate normal distribution graph length
        double xMin = -4.0, xMax = 4.0;
        double yMin = -4.0, yMax = 4.0;

        // Size of one rectangle in the grid
        int resolution = 1000;
        double stepX = (xMax - xMin) / resolution;
        double stepY = (yMax - yMin) / resolution;

        // Filling the grid with rectangles
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                double x = xMin + i * stepX;
                double y = yMin + j * stepY;
                double density = callback.getNewDensity(x, y);

                // Transform density to color (the higher the density, the darker the color)
                int intensity = (int) (255 * Math.min(density * 10, 1.0));
                g2d.setColor(new Color(255-intensity, intensity,intensity));

                // Transform coordinates from mathematical system to pixels
                int pixelX = (int) ((x - xMin) / (xMax - xMin) * width);
                int pixelY = (int) ((yMax - y) / (yMax - yMin) * height);
                g2d.fill(new Rectangle2D.Double(pixelX, pixelY, width / resolution + 1, height / resolution + 1));
            }
        }
    }
}
