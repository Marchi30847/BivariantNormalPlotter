import java.awt.*;
import javax.swing.*;

public class GraphPlotter extends JPanel {
    BivariateNormalDistribution dist;
    double a, b;

    public GraphPlotter(BivariateNormalDistribution dist, double a, double b) {
        this.dist = dist;
        this.a = a;
        this.b = b;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double x = (double) i / width * 10;
                double y = (double) j / height * 10;

                double probability = ConditionalProbability.computeConditionalProbability(x, y, dist);
                int colorValue = (int) (probability * 255);
                g2d.setColor(new Color(colorValue, colorValue, colorValue));
                g2d.fillRect(i, j, 1, 1);
            }
        }

        // Output probability F(a, b)
        double probability = ConditionalProbability.computeConditionalProbability(a, b, dist);
        System.out.println("F(a, b) = " + probability);
    }
}