package deep;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.ProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class Gui3d extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("3D Bivariate Normal Distribution Plot");

		// Create UI components
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

		// Progress indicator to show loading status
		ProgressIndicator progressIndicator = new ProgressIndicator();
		progressIndicator.setVisible(false); // Initially hidden

		// Input panel layout
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

		// Chart panel for 3D plot
		VBox chartPanel = new VBox();
		chartPanel.setStyle("-fx-background-color: lightgray; -fx-pref-width: 500; -fx-pref-height: 500;");

		// Group for 3D objects
		Group root3D = new Group();
		SubScene subScene = new SubScene(root3D, 500, 500, true, SceneAntialiasing.BALANCED);

		// Add SubScene (3D plot) to chart panel
		chartPanel.getChildren().add(subScene);

		// Main layout
		VBox mainLayout = new VBox(10);
		mainLayout.getChildren().addAll(inputGrid, chartPanel);

		// Scene
		Scene scene = new Scene(mainLayout, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		computeButton.setOnAction(e -> {
			try {
				double meanX = Double.parseDouble(fieldMeanX.getText());
				double meanY = Double.parseDouble(fieldMeanY.getText());
				double sigmaX = Double.parseDouble(fieldSigmaX.getText());
				double sigmaY = Double.parseDouble(fieldSigmaY.getText());
				double rho = Double.parseDouble(fieldRho.getText());
				double a = Double.parseDouble(fieldA.getText()); // Lower bound for visualization
				double b = Double.parseDouble(fieldB.getText()); // Upper bound for visualization

				// Validate input
				if (rho < -1 || rho > 1 || sigmaX <= 0 || sigmaY <= 0) {
					throw new IllegalArgumentException("Invalid parameters: rho must be between -1 and 1, sigma must be positive.");
				}

				progressIndicator.setVisible(true);

				Task<MeshView> task = new Task<>() {
					@Override
					protected MeshView call() throws Exception {
						return generateBivariateNormalPlot(a, b, meanX, meanY, sigmaX, sigmaY, rho);
					}

					@Override
					protected void succeeded() {
						super.succeeded();
						Platform.runLater(() -> {
							MeshView plot = getValue();
							root3D.getChildren().clear();
							root3D.getChildren().add(plot);
							progressIndicator.setVisible(false);
							subScene.requestFocus();
						});
					}

					@Override
					protected void failed() {
						progressIndicator.setVisible(false);
						Throwable ex = getException();
						showError("Plot Generation Failed", "An error occurred: " + ex.getMessage());
					}
				};

				new Thread(task).start();

			} catch (NumberFormatException ex) {
				showError("Invalid Input", "Please enter valid numerical inputs.");
			} catch (IllegalArgumentException ex) {
				showError("Validation Error", ex.getMessage());
			}
		});
	}

	private void showError(String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	private MeshView generateBivariateNormalPlot(double a, double b, double meanX, double meanY, double sigmaX, double sigmaY, double rho) {
		TriangleMesh mesh = new TriangleMesh();
		List<Float> pointsList = new ArrayList<>();
		List<Integer> facesList = new ArrayList<>();

		// Use a dynamic step size for better detail where needed
		double step = 0.5;
		double range = 3;

		for (double x = -range; x <= range; x += step) {
			for (double y = -range; y <= range; y += step) {
				double z = BivariateNormalLogic.bivariateNormalPDF(x, y, meanX, meanY, sigmaX, sigmaY, rho) * 100;
				pointsList.add((float)x);
				pointsList.add((float)y);
				pointsList.add((float)z);
			}
		}

		float[] points = new float[pointsList.size()];
		for (int i = 0; i < pointsList.size(); i++) {
			points[i] = pointsList.get(i);
		}

		int[] faces = generateFaces(points);

		mesh.getPoints().addAll(points);
		mesh.getFaces().addAll(faces);

		MeshView meshView = new MeshView(mesh);
		meshView.setMaterial(new PhongMaterial(Color.BLUE));
		meshView.setDrawMode(DrawMode.FILL);
		meshView.setTranslateX(250);
		meshView.setTranslateY(250);
		meshView.setTranslateZ(-50);

		meshView.setScaleX(50);
		meshView.setScaleY(50);

		meshView.getTransforms().add(new Rotate(45, Rotate.Y_AXIS));
		meshView.getTransforms().add(new Rotate(-30, Rotate.X_AXIS));

		return meshView;
	}



	private int[] generateFaces(float[] points) {
		int numPoints = points.length / 3; // Each point is represented by x, y, z
		int[] faces = new int[(numPoints - 1) * (numPoints - 1) * 6]; // 6 indices per quad for two triangles
		int faceIndex = 0;
		for (int i = 0; i < numPoints - 1; i++) {
			for (int j = 0; j < numPoints - 1; j++) {
				// Calculate indices based on grid structure
				int p0 = i * numPoints + j;
				int p1 = p0 + 1;
				int p2 = p0 + numPoints;
				int p3 = p2 + 1;

				// First triangle of the quad
				faces[faceIndex++] = p0; faces[faceIndex++] = 0; // Point index, texture coordinate index
				faces[faceIndex++] = p1; faces[faceIndex++] = 0;
				faces[faceIndex++] = p2; faces[faceIndex++] = 0;

				// Second triangle of the quad
				faces[faceIndex++] = p2; faces[faceIndex++] = 0;
				faces[faceIndex++] = p1; faces[faceIndex++] = 0;
				faces[faceIndex++] = p3; faces[faceIndex++] = 0;
			}
		}
		return faces;
	}

}
