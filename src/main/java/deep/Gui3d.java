package deep;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Gui3d extends Application {

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Bivariate Normal Distribution");

		// Create the UI components
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

		// Input panel layout
		GridPane inputGrid = new GridPane();
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

		// Chart panel (this is where the 3D chart should go, but for now, we'll leave it empty)
		VBox chartPanel = new VBox();
		chartPanel.setStyle("-fx-background-color: lightgray; -fx-pref-width: 500; -fx-pref-height: 500;");

		// deep.Main layout
		VBox mainLayout = new VBox(10);
		mainLayout.getChildren().addAll(inputGrid, chartPanel);

		// Scene
		Scene scene = new Scene(mainLayout, 800, 600);
		primaryStage.setScene(scene);
		primaryStage.show();

		computeButton.setOnAction(e -> {
			try {
				// Parse the user inputs
				double a = Double.parseDouble(fieldA.getText());
				double b = Double.parseDouble(fieldB.getText());
				double meanX = Double.parseDouble(fieldMeanX.getText());
				double meanY = Double.parseDouble(fieldMeanY.getText());
				double sigmaX = Double.parseDouble(fieldSigmaX.getText());
				double sigmaY = Double.parseDouble(fieldSigmaY.getText());
				double rho = Double.parseDouble(fieldRho.getText());

				// TODO: Replace this with actual 3D plotting logic (this part is skipped for now)
				System.out.println("Computed Bivariate Normal Distribution with the following parameters:");
				System.out.println("a: " + a + ", b: " + b + ", Mean X: " + meanX + ", Mean Y: " + meanY + ", Sigma X: " + sigmaX + ", Sigma Y: " + sigmaY + ", Rho: " + rho);

				// You should update the chartPanel here with a valid 3D plot (using JavaFX 3D graphics, or another compatible library)

			} catch (NumberFormatException ex) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Please enter valid numerical inputs.");
				alert.showAndWait();
			}
		});
	}

}
