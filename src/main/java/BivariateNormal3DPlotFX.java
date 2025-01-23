import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BivariateNormal3DPlotFX extends Application {

	@Override
	public void start(Stage primaryStage) {
		// Parameters for the bivariate normal distribution
		double meanX = 0.0, meanY = 0.0, sigmaX = 1.0, sigmaY = 1.0, rho = 0.5;
		double aMin = -3.0, aMax = 3.0, bMin = -3.0, bMax = 3.0;
		int resolution = 20;

		// Create a group to hold all 3D shapes
		Group root = new Group();

		// Loop through the range of a and b values to compute the conditional probability
		for (int i = 0; i < resolution; i++) {
			for (int j = 0; j < resolution; j++) {
				double a = aMin + (aMax - aMin) * i / (resolution - 1);
				double b = bMin + (bMax - bMin) * j / (resolution - 1);

				// Compute the conditional probability using the provided method
				double prob = BivariateNormalLogic.computeConditionalProbability(a, b, meanX, meanY, sigmaX, sigmaY, rho);

				// Create a 3D shape (cylinder here) for each (a, b) pair and add it to the scene
				double height = prob * 10; // scale the height for better visibility
				Cylinder cylinder = new Cylinder(0.05, height);
				cylinder.setTranslateX(a * 50); // Scaling for visual spacing
				cylinder.setTranslateY(b * 50); // Scaling for visual spacing
				cylinder.setTranslateZ(height / 2); // Position in Z direction based on height

				if (prob > 0.5) {
					cylinder.setMaterial(new javafx.scene.paint.PhongMaterial(Color.GREEN));
				} else {
					cylinder.setMaterial(new javafx.scene.paint.PhongMaterial(Color.RED));
				}

				// Add the cylinder to the root
				root.getChildren().add(cylinder);
			}
		}

		// Add axis labels
		Text xLabel = new Text("X (a)");
		xLabel.setTranslateX(300);
		xLabel.setTranslateY(300);
		Text yLabel = new Text("Y (b)");
		yLabel.setTranslateX(300);
		yLabel.setTranslateY(350);

		root.getChildren().add(xLabel);
		root.getChildren().add(yLabel);

		// Create the camera for the 3D scene
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateZ(-500);

		// Set up the scene and stage
		Scene scene = new Scene(root, 800, 600, true);
		scene.setFill(Color.LIGHTGRAY);
		scene.setCamera(camera);

		primaryStage.setTitle("3D Conditional Probability Plot");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}