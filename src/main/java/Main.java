//Hello guys

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        BivariateNormalDistribution dist = new BivariateNormalDistribution(0, 0, 1, 1, 0.5);
        double a = 1.0;
        double b = 1.0;

        JFrame frame = new JFrame("Bivariate Normal Distribution Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.setLocationRelativeTo(null);   // center the window
        GraphPlotter plotter = new GraphPlotter(dist, a, b);
        frame.add(plotter);
        frame.setVisible(true);
    }
}
