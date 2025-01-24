/**
 * A JavaFX application that plots the function F(x, y) = P(X > x | Y > y) 
 * for a user-specified bivariate normal distribution, and displays the value 
 * F(a, b) = P(X > a | Y > b).
 */
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class Main extends Application {
    private TextField tfMuX     = new TextField("0.0");
    private TextField tfMuY     = new TextField("0.0");
    private TextField tfSigmaX  = new TextField("1.0");
    private TextField tfSigmaY  = new TextField("1.0");
    private TextField tfRho     = new TextField("0.5");
    private TextField tfA       = new TextField("0.5");
    private TextField tfB       = new TextField("0.5");
    private Label     lblResult = new Label("F(a, b) = ?");

    private Group     plotGroup = new Group();
    private static final double X_MIN = -3.0;
    private static final double X_MAX =  3.0;
    private static final double Y_MIN = -3.0;
    private static final double Y_MAX =  3.0;
    private static final int    STEPS_X = 60;
    private static final int    STEPS_Y = 60;
    private static final double TAIL_MULTIPLIER = 5.0;

    /**
     * Builds a 3D surface mesh of F(x, y) = P(X > x | Y > y) 
     * for the given parameters over a grid in [X_MIN, X_MAX] x [Y_MIN, Y_MAX].
     * @param muX mean of X
     * @param muY mean of Y
     * @param sigmaX std. dev. of X
     * @param sigmaY std. dev. of Y
     * @param rho correlation
     * @return TriangleMesh for the surface
     */
    private TriangleMesh buildSurfaceMesh(
            double muX, double muY, double sigmaX, double sigmaY, double rho)
    {
        TriangleMesh mesh = new TriangleMesh();
        int pointCountX = STEPS_X + 1;
        int pointCountY = STEPS_Y + 1;
        double dx = (X_MAX - X_MIN) / STEPS_X;
        double dy = (Y_MAX - Y_MIN) / STEPS_Y;

        for (int iy = 0; iy < pointCountY; iy++) {
            double yVal = Y_MIN + iy * dy;
            for (int ix = 0; ix < pointCountX; ix++) {
                double xVal = X_MIN + ix * dx;
                float zVal = (float) conditionalTailProb(xVal, yVal, muX, muY, sigmaX, sigmaY, rho);
                mesh.getPoints().addAll((float)xVal, (float)yVal, zVal);
            }
        }
        for (int iy = 0; iy < pointCountY; iy++) {
            float v = (float) iy / (pointCountY - 1);
            for (int ix = 0; ix < pointCountX; ix++) {
                float u = (float) ix / (pointCountX - 1);
                mesh.getTexCoords().addAll(u, v);
            }
        }
        for (int iy = 0; iy < STEPS_Y; iy++) {
            for (int ix = 0; ix < STEPS_X; ix++) {
                int p0 = iy * pointCountX + ix;
                int p1 = p0 + 1;
                int p2 = p0 + pointCountX;
                int p3 = p2 + 1;
                mesh.getFaces().addAll(p0, p0, p1, p1, p2, p2);
                mesh.getFaces().addAll(p1, p1, p3, p3, p2, p2);
            }
        }
        return mesh;
    }

    /**
     * Computes F(x, y) = P(X > x | Y > y).
     * @param x threshold for X
     * @param y threshold for Y
     * @param muX mean of X
     * @param muY mean of Y
     * @param sigmaX std. dev. of X
     * @param sigmaY std. dev. of Y
     * @param rho correlation
     * @return the conditional probability value
     */
    private double conditionalTailProb(double x, double y,
                                       double muX, double muY,
                                       double sigmaX, double sigmaY,
                                       double rho)
    {
        double denom = tailProbabilityUnivariate(y, muY, sigmaY);
        if (denom < 1e-15) return 0.0;
        double numerator = jointTailProbability(x, y, muX, muY, sigmaX, sigmaY, rho);
        return numerator / denom;
    }

    /**
     * Computes the univariate normal tail probability P(Y > y).
     * @param yLower threshold
     * @param muY mean of Y
     * @param sigmaY std. dev. of Y
     * @return probability that Y > yLower
     */
    private double tailProbabilityUnivariate(double yLower, double muY, double sigmaY) {
        double yMax = muY + TAIL_MULTIPLIER * sigmaY;
        if (yLower >= yMax) return 0.0;
        int N = 200;
        double dy = (yMax - yLower) / N;
        double sum = 0.0;
        for (int j = 0; j < N; j++) {
            double yy = yLower + (j + 0.5) * dy;
            sum += univariateNormalPDF(yy, muY, sigmaY);
        }
        return sum * dy;
    }

    /**
     * Computes the joint tail probability P(X > xLower, Y > yLower).
     * @param xLower threshold for X
     * @param yLower threshold for Y
     * @param muX mean of X
     * @param muY mean of Y
     * @param sigmaX std. dev. of X
     * @param sigmaY std. dev. of Y
     * @param rho correlation
     * @return the joint probability that X > xLower and Y > yLower
     */
    private double jointTailProbability(double xLower, double yLower,
                                        double muX, double muY,
                                        double sigmaX, double sigmaY,
                                        double rho)
    {
        double xMax = muX + TAIL_MULTIPLIER * sigmaX;
        double yMax = muY + TAIL_MULTIPLIER * sigmaY;
        if (xLower >= xMax || yLower >= yMax) return 0.0;
        int Nx = 100;
        int Ny = 100;
        double dx = (xMax - xLower) / Nx;
        double dy = (yMax - yLower) / Ny;
        double sum = 0.0;
        for (int i = 0; i < Nx; i++) {
            double xx = xLower + (i + 0.5) * dx;
            for (int j = 0; j < Ny; j++) {
                double yy = yLower + (j + 0.5) * dy;
                sum += bivariateNormalPDF(xx, yy, muX, muY, sigmaX, sigmaY, rho);
            }
        }
        return sum * dx * dy;
    }

    /**
     * Returns the univariate normal PDF at y.
     * @param y the point at which PDF is evaluated
     * @param mu mean
     * @param sigma standard deviation
     * @return normal PDF value at y
     */
    private double univariateNormalPDF(double y, double mu, double sigma) {
        double z = (y - mu) / sigma;
        return (1.0 / (Math.sqrt(2.0 * Math.PI) * sigma)) * Math.exp(-0.5 * z * z);
    }

    /**
     * Returns the bivariate normal PDF at (x, y).
     * @param x point X
     * @param y point Y
     * @param muX mean of X
     * @param muY mean of Y
     * @param sigmaX std dev of X
     * @param sigmaY std dev of Y
     * @param rho correlation
     * @return bivariate normal PDF at (x, y)
     */
    private double bivariateNormalPDF(double x, double y,
                                      double muX, double muY,
                                      double sigmaX, double sigmaY,
                                      double rho)
    {
        double norm = 1.0 / (2.0 * Math.PI * sigmaX * sigmaY * Math.sqrt(1 - rho*rho));
        double zx = (x - muX) / sigmaX;
        double zy = (y - muY) / sigmaY;
        double e = -1.0/(2*(1-rho*rho)) * (zx*zx + zy*zy - 2*rho*zx*zy);
        return norm * Math.exp(e);
    }

    /**
     * Refreshes the 3D plot by reading the parameters from the text fields
     * and updating the surface mesh and the label for F(a,b).
     */
    private void refreshPlot() {
        double muX    = Double.parseDouble(tfMuX.getText());
        double muY    = Double.parseDouble(tfMuY.getText());
        double sX     = Double.parseDouble(tfSigmaX.getText());
        double sY     = Double.parseDouble(tfSigmaY.getText());
        double rho    = Double.parseDouble(tfRho.getText());
        double a      = Double.parseDouble(tfA.getText());
        double b      = Double.parseDouble(tfB.getText());

        TriangleMesh mesh = buildSurfaceMesh(muX, muY, sX, sY, rho);
        MeshView surface = new MeshView(mesh);
        surface.setMaterial(new PhongMaterial(Color.SKYBLUE));
        surface.getTransforms().addAll(
                new Rotate(-45, Rotate.X_AXIS),
                new Rotate( 45, Rotate.Y_AXIS),
                new Translate(0, 0, -10)
        );

        plotGroup.getChildren().clear();
        plotGroup.getChildren().add(surface);

        double val = conditionalTailProb(a, b, muX, muY, sX, sY, rho);
        lblResult.setText(String.format("F(a, b) = %.6f", val));
    }

    /**
     * Main entry point for the JavaFX application.
     * @param stage primary stage
     */
    @Override
    public void start(Stage stage) {
        FlowPane topPane = new FlowPane();
        topPane.setHgap(10);
        topPane.getChildren().addAll(
                new Label("muX:"), tfMuX,
                new Label("muY:"), tfMuY,
                new Label("sigmaX:"), tfSigmaX,
                new Label("sigmaY:"), tfSigmaY,
                new Label("rho:"), tfRho,
                new Label("a:"), tfA,
                new Label("b:"), tfB
        );
        Button btnRefresh = new Button("Refresh");
        topPane.getChildren().add(btnRefresh);
        btnRefresh.setOnAction(e -> refreshPlot());

        SubScene subScene = new SubScene(plotGroup, 800, 600, true, SceneAntialiasing.BALANCED);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setFarClip(1000);
        camera.setTranslateZ(-50);
        subScene.setCamera(camera);

        BorderPane root = new BorderPane();
        root.setTop(topPane);
        root.setCenter(subScene);
        root.setBottom(lblResult);

        stage.setScene(new Scene(root, 1000, 700));
        stage.setTitle("Bivariate Normal: P(X > x | Y > y)");
        stage.show();

        refreshPlot();
    }

    /**
     * Main method to launch the JavaFX application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}