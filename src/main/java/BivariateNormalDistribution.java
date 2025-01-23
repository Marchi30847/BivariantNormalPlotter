import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BivariateNormalDistribution extends JPanel {

    // Параметры для двумерного нормального распределения
    private static final double meanX = 0.0;
    private static final double meanY = 0.0;
    private static final double stdX = 1.0;
    private static final double stdY = 1.0;
    private static final double correlation = 0.5;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Настройки
        int width = getWidth();
        int height = getHeight();

        // Границы графика
        double xMin = -3.0, xMax = 3.0;
        double yMin = -3.0, yMax = 3.0;

        // Разрешение сетки
        int resolution = 200;
        double stepX = (xMax - xMin) / resolution;
        double stepY = (yMax - yMin) / resolution;

        // Вычисляем плотность и рисуем прямоугольники
        for (int i = 0; i < resolution; i++) {
            for (int j = 0; j < resolution; j++) {
                double x = xMin + i * stepX;
                double y = yMin + j * stepY;
                double density = bivariateNormalDensity(x, y);

                // Преобразуем плотность в цвет (чем выше, тем темнее)
                int intensity = (int) (255 * Math.min(density * 10, 1.0));
                g2d.setColor(new Color(255-intensity, intensity,intensity));

                // Преобразуем координаты из математической системы в пиксели
                int pixelX = (int) ((x - xMin) / (xMax - xMin) * width);
                int pixelY = (int) ((yMax - y) / (yMax - yMin) * height);
                g2d.fill(new Rectangle2D.Double(pixelX, pixelY, width / resolution + 1, height / resolution + 1));

            }
        }
    }

    // Функция для вычисления плотности двумерного нормального распределения
    private double bivariateNormalDensity(double x, double y) {
        double covarianceXY = correlation * stdX * stdY;
        double determinant = (stdX * stdX) * (stdY * stdY) - covarianceXY * covarianceXY;

        double normalization = 1.0 / (2 * Math.PI * Math.sqrt(determinant));
        double zX = (x - meanX) / stdX;
        double zY = (y - meanY) / stdY;

        double exponent = -0.5 * ((zX * zX) - 2 * correlation * zX * zY + (zY * zY)) / (1 - correlation * correlation);
        return normalization * Math.exp(exponent);
    }

    // Главный метод
    public static void main(String[] args) {
        JFrame frame = new JFrame("Bivariate Normal Distribution");
        BivariateNormalDistribution panel = new BivariateNormalDistribution();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 800);
        frame.add(panel);
        frame.setVisible(true);
    }
}
