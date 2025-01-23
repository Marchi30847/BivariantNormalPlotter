package ui;

import domain.BivariantNormalCalculator;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class BivariantNormal3DPlotter extends Application {

    private static double meanX, meanY, stdX, stdY, correlation;

    public static void setParameters(double mX, double mY, double sX, double sY, double corr) {
        meanX = mX;
        meanY = mY;
        stdX = sX;
        stdY = sY;
        correlation = corr;
    }

    @Override
    public void start(Stage stage) {
        // Create the 3D surface mesh
        TriangleMesh mesh = createSurface();

        // Set material for visualization
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.LIGHTBLUE);
        material.setSpecularColor(Color.LIGHTGRAY);

        MeshView surface = new MeshView(mesh);
        surface.setMaterial(material);

        // Rotate the graph for better view
        surface.getTransforms().add(new Rotate(-30, Rotate.X_AXIS));
        surface.getTransforms().add(new Rotate(-45, Rotate.Y_AXIS));

        // Group and scene setup
        Group root = new Group(surface);
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);

        // Add perspective camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-300);
        scene.setCamera(camera);

        // Setup stage
        stage.setTitle("3D Bivariate Normal Distribution");
        stage.setScene(scene);
        stage.show();
    }

    private TriangleMesh createSurface() {
        int steps = 50;
        double range = 3.0; // X and Y range from -3 to 3

        TriangleMesh mesh = new TriangleMesh();

        // Generate vertices
        for (int i = 0; i <= steps; i++) {
            for (int j = 0; j <= steps; j++) {
                double x = -range + 2 * range * i / steps;
                double y = -range + 2 * range * j / steps;
                double z = computeConditionalProbabilityZ(x, y);
                mesh.getPoints().addAll((float) x, (float) y, (float) z * 100); // Scale Z
            }
        }

        // Generate faces
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                int p0 = i * (steps + 1) + j;
                int p1 = i * (steps + 1) + (j + 1);
                int p2 = (i + 1) * (steps + 1) + j;
                int p3 = (i + 1) * (steps + 1) + (j + 1);

                // Add two triangles for each grid square
                mesh.getFaces().addAll(p0, 0, p1, 0, p2, 0);
                mesh.getFaces().addAll(p1, 0, p3, 0, p2, 0);
            }
        }

        return mesh;
    }

    private double computeConditionalProbabilityZ(double x, double y) {
        return BivariantNormalCalculator.computeConditionalProbability(meanX, meanY, stdX, stdY, correlation, x, y);
    }
}