package deep.ui;

import deep.data.PlotterContract;
import deep.domain.BivariateNormalModel;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.Stage;
import javafx.scene.Group;

import java.util.ArrayList;
import java.util.List;

public class PlotterView extends Application implements PlotterContract.View {

    private Scene scene;
    private HBox mainLayout = new HBox();
    private Group root3D = new Group();
    private SubScene subScene;
    private ScrollPane scrollPane;
    private VBox inputPanel;
    private VBox plotPanel;
    private Button computeButton;

    private final List<TextField> inputFields = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("3D Bivariate Normal Distribution Plot");

        configureRoot3D();
        configurePlotPanel();
        configureSubScene();
        configureComputeButton();
        configureInputPanel();
        configureScrollPane();

        configureMainLayout();
        configureScene();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void configureScene() {
        scene = new Scene(mainLayout, 1920, 1080);

    }

    private void configureMainLayout() {
        mainLayout = new HBox(plotPanel, scrollPane);
        HBox.setHgrow(plotPanel, Priority.ALWAYS);
    }

    private void configureRoot3D() {
        root3D = new Group();
    }

    private void configureSubScene() {
        subScene = new SubScene(root3D, 1080, 1080, true, null);
        subScene.setStyle("-fx-background-color: lightgray;");
        subScene.widthProperty().bind(plotPanel.widthProperty());
        subScene.heightProperty().bind(plotPanel.heightProperty());
    }

    private void configurePlotPanel() {
        plotPanel = new VBox(20);
        plotPanel.setStyle("-fx-background-color: lightgray;");
        plotPanel.setPrefWidth(0);
        plotPanel.setMaxWidth(Double.MAX_VALUE);
    }

    private void configureComputeButton() {
        computeButton = new Button("Compute (Enter)");
        computeButton.setStyle("-fx-font-size: 14px;");
        setComputeListener(_ -> computeAndPlot(root3D));
    }

    private void configureInputPanel() {
        inputPanel = new VBox(20);
        inputPanel.setPadding(new Insets(10));
        inputPanel.setPrefWidth(300);
        inputPanel.setStyle("-fx-background-color: #f4f4f4;");

        Label panelTitle = new Label("Input Parameters");
        panelTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox integrationBoundsSection = createInputSection(
                "Integration Bounds",
                new String[]{"Lower bound (a):", "Upper bound (b):"}
        );
        VBox meansSection = createInputSection(
                "Means",
                new String[]{"Mean X:", "Mean Y:"}
        );
        VBox variancesSection = createInputSection(
                "Variances",
                new String[]{"Sigma X:", "Sigma Y:"}
        );
        VBox covarianceSection = createInputSection(
                "Covariance",
                new String[]{"Correlation (rho):"}
        );

        inputPanel.getChildren().addAll(
                panelTitle,
                integrationBoundsSection,
                meansSection,
                variancesSection,
                covarianceSection,
                computeButton
        );

        scrollPane = new ScrollPane(inputPanel);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setPrefWidth(300);
    }

    private VBox createInputSection(String title, String[] labels) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(5));
        section.setStyle("-fx-border-color: #ddd; -fx-border-width: 1; -fx-padding: 10;");

        Label sectionTitle = new Label(title);
        sectionTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        section.getChildren().add(sectionTitle);

        for (String label : labels) {
            Label inputLabel = new Label(label);
            TextField textField = new TextField();
            inputFields.add(textField);

            VBox inputBox = new VBox(5, inputLabel, textField);
            section.getChildren().add(inputBox);
        }

        return section;
    }

    private void configureScrollPane() {
        scrollPane = new ScrollPane(inputPanel);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        scrollPane.setPrefWidth(300);
    }

    private void computeAndPlot(Group root3D) {
        try {
            double a = Double.parseDouble(inputFields.get(0).getText());
            double b = Double.parseDouble(inputFields.get(1).getText());
            double meanX = Double.parseDouble(inputFields.get(2).getText());
            double meanY = Double.parseDouble(inputFields.get(3).getText());
            double sigmaX = Double.parseDouble(inputFields.get(4).getText());
            double sigmaY = Double.parseDouble(inputFields.get(5).getText());
            double rho = Double.parseDouble(inputFields.get(6).getText());

            if (rho < -1 || rho > 1 || sigmaX <= 0 || sigmaY <= 0) {
                throw new IllegalArgumentException("Invalid parameters: rho must be between -1 and 1, sigma must be positive.");
            }

            MeshView meshView = generateBivariateNormalPlot(a, b, meanX, meanY, sigmaX, sigmaY, rho);

            root3D.getChildren().clear();
            root3D.getChildren().add(meshView);

        } catch (NumberFormatException ex) {
            showError("Invalid Input", "Please enter valid numerical inputs.");
        } catch (IllegalArgumentException ex) {
            showError("Validation Error", ex.getMessage());
        }
    }

    private MeshView generateBivariateNormalPlot(
            double a, double b,
            double meanX, double meanY,
            double sigmaX, double sigmaY,
            double rho
    ) {
        TriangleMesh mesh = new TriangleMesh();
        List<Float> pointsList = new ArrayList<>();

        double step = 0.5;
        for (double x = a; x <= b; x += step) {
            for (double y = a; y <= b; y += step) {
                double z = BivariateNormalModel.bivariateNormalPDF(x, y, meanX, meanY, sigmaX, sigmaY, rho) * 100;
                pointsList.add((float) x);
                pointsList.add((float) y);
                pointsList.add((float) z);
            }
        }

        float[] points = new float[pointsList.size()];
        for (int i = 0; i < pointsList.size(); i++) {
            points[i] = pointsList.get(i);
        }

        mesh.getPoints().addAll(points);

        MeshView meshView = new MeshView(mesh);
        meshView.setMaterial(new PhongMaterial(Color.BLUE));
        meshView.setDrawMode(DrawMode.FILL);
        return meshView;
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void setComputeListener(EventHandler<ActionEvent> eventHandler) {
        computeButton.setOnAction(eventHandler);
    }
}