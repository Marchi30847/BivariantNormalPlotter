package deep;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class Gui3d extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("3D Bivariate Normal Distribution Plot");

		// UI Components
		Label labelA = new Label("a:");
		TextField fieldA = new TextField();
		Label labelB = new Label("b:");
		TextField fieldB = new TextField();
		Label labelMeanX = new Label("Mean X:");
		TextField fieldMeanX = new TextField();
		Label labelMeanY = new Label("Mean Y:");
		TextField fieldMeanY = new TextField();
		Label labelSigmaX = new Label("Sigma X:");
		TextField fieldSigmaX = new TextField();
		Label labelSigmaY = new Label("Sigma Y:");
		TextField fieldSigmaY = new TextField();
		Label labelRho = new Label("Correlation (rho):");
		TextField fieldRho = new TextField();
		Button computeButton = new Button("Compute and Plot");
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setVisible(false);

		// Layout
		GridPane inputGrid = new GridPane();
		inputGrid.setHgap(10);
		inputGrid.setVgap(10);
		inputGrid.add(labelA, 0, 0);
		inputGrid.add(fieldA, 1, 0);
		inputGrid.add(labelB, 0, 1);
		inputGrid.add(fieldB, 1, 1);
		inputGrid.add(labelMeanX, 0, 2);
		inputGrid.add(fieldMeanX, 1, 2);
		inputGrid.add(labelMeanY, 0, 3);
		inputGrid.add(fieldMeanY, 1, 3);
		inputGrid.add(labelSigmaX, 0, 4);
		inputGrid.add(fieldSigmaX, 1, 4);
		inputGrid.add(labelSigmaY, 0, 5);
		inputGrid.add(fieldSigmaY, 1, 5);
		inputGrid.add(labelRho, 0, 6);
		inputGrid.add(fieldRho, 1, 6);
		inputGrid.add(computeButton, 0, 7, 2, 1);
		inputGrid.add(progressIndicator, 0, 8, 2, 1);

		VBox chartPanel = new VBox();
		chartPanel.setStyle("-fx-background-color: lightgray; -fx-pref-width: 500; -fx-pref-height: 500;");

		Group root3D = new Group();
		SubScene subScene = new SubScene(root3D, 500, 500, true, SceneAntialiasing.BALANCED);
		chartPanel.getChildren().add(subScene);

		VBox mainLayout = new VBox(10);
		mainLayout.getChildren().addAll(inputGrid, chartPanel);

		Scene scene = new Scene(mainLayout, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		// Camera and Lighting
		PerspectiveCamera camera = new PerspectiveCamera(true);
		camera.setTranslateZ(-1000);
		subScene.setCamera(camera);

		AmbientLight light = new AmbientLight(Color.WHITE);
		root3D.getChildren().add(light);



		computeButton.setOnAction(e -> {
			try {
				double meanX = Double.parseDouble(fieldMeanX.getText());
				double meanY = Double.parseDouble(fieldMeanY.getText());
				double sigmaX = Double.parseDouble(fieldSigmaX.getText());
				double sigmaY = Double.parseDouble(fieldSigmaY.getText());
				double rho = Double.parseDouble(fieldRho.getText());
				double a = Double.parseDouble(fieldA.getText());
				double b = Double.parseDouble(fieldB.getText());

				if (rho < -1 || rho > 1 || sigmaX <= 0 || sigmaY <= 0) {
					throw new IllegalArgumentException("Invalid parameters: rho must be between -1 and 1, sigma must be positive.");
				}

				progressIndicator.setVisible(true);

				Task<MeshView> task = new Task<>() {
//					@Override
//					protected MeshView call() {
//						return generateBivariateNormalPlot(a, b, meanX, meanY, sigmaX, sigmaY, rho);
//					}
					@Override
					protected MeshView call() {
						// Debugging: generate a basic cube to test the 3D rendering
						System.out.println("Generating plot...");
						MeshView plot = generateBivariateNormalPlot(a, b, meanX, meanY, sigmaX, sigmaY, rho);
						System.out.println("Plot generated successfully!");
						return plot;
					}


					@Override
					protected void succeeded() {
						super.succeeded();
						Platform.runLater(() -> {
							MeshView plot = getValue();
							System.out.println("MeshView generated successfully!");
							root3D.getChildren().clear();
							root3D.getChildren().addAll(plot, light);
							progressIndicator.setVisible(false);
							subScene.requestFocus();

							// Debugging log to confirm objects are being added
							System.out.println("Number of objects in root3D: " + root3D.getChildren().size());
						});
					}


					@Override
					protected void failed() {
						progressIndicator.setVisible(false);
						showError("Plot Generation Failed", "An error occurred: " + getException().getMessage());
						getException().printStackTrace();  // Log the exception
					}
				};

				new Thread(task).start();
			} catch (Exception ex) {
				showError("Invalid Input", "Please enter valid numerical inputs.");
				ex.printStackTrace();  // Log the exception for debugging
			}
		});

	}


	private MeshView generateBivariateNormalPlot(double a, double b, double meanX, double meanY, double sigmaX, double sigmaY, double rho) {
		TriangleMesh mesh = new TriangleMesh();
		double rangeFactor = 2;
		int steps = 10;

		double xMin = meanX - rangeFactor * sigmaX;
		double xMax = meanX + rangeFactor * sigmaX;
		double yMin = meanY - rangeFactor * sigmaY;
		double yMax = meanY + rangeFactor * sigmaY;

		float[] points = new float[(steps + 1) * (steps + 1) * 3];
		int pointIndex = 0;

		for (int i = 0; i <= steps; i++) {
			for (int j = 0; j <= steps; j++) {
				double x = xMin + i * (xMax - xMin) / steps;
				double y = yMin + j * (yMax - yMin) / steps;
				double z = BivariateNormalLogic.bivariateNormalPDF(x, y, meanX, meanY, sigmaX, sigmaY, rho) * 10000; // Larger scaling for visualization
				points[pointIndex++] = (float) x * 2;  // Scale x for better visibility
				points[pointIndex++] = (float) y * 2;  // Scale y for better visibility
				points[pointIndex++] = (float) z;
			}
		}

		int[] faces = generateFaces(points, steps);
		mesh.getPoints().addAll(points);
		mesh.getFaces().addAll(faces);

		// Debugging: Log number of points and faces
		System.out.println("Mesh Points: " + points.length / 3);
		System.out.println("Mesh Faces: " + faces.length / 6);

		MeshView meshView = new MeshView(mesh);
		meshView.setMaterial(new PhongMaterial(Color.BLUE));
		meshView.setDrawMode(DrawMode.FILL);
		meshView.setCullFace(CullFace.NONE);  // Show both sides of triangles
		meshView.setTranslateX(250);  // Center the plot
		meshView.setTranslateY(250);
		meshView.setTranslateZ(-200);  // Adjust for camera distance

		// Add rotation for better viewing
		meshView.getTransforms().add(new Rotate(45, Rotate.Y_AXIS));
		meshView.getTransforms().add(new Rotate(-30, Rotate.X_AXIS));

		return meshView;
	}


	private int[] generateFaces(float[] points, int steps) {
		List<Integer> facesList = new ArrayList<>();
		for (int i = 0; i < steps; i++) {
			for (int j = 0; j < steps; j++) {
				int p0 = i * (steps + 1) + j;
				int p1 = p0 + 1;
				int p2 = p0 + (steps + 1);
				int p3 = p2 + 1;

				// First triangle
				facesList.add(p0); facesList.add(0);
				facesList.add(p1); facesList.add(0);
				facesList.add(p2); facesList.add(0);

				// Second triangle
				facesList.add(p2); facesList.add(0);
				facesList.add(p1); facesList.add(0);
				facesList.add(p3); facesList.add(0);
			}
		}
		return facesList.stream().mapToInt(i -> i).toArray();
	}

	private void showError(String title, String message) {
		Platform.runLater(() -> {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle(title);
			alert.setHeaderText(null);
			alert.setContentText(message);
			alert.showAndWait();
		});
	}
}
