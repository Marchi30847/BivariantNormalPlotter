import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

/**
 * A JavaFX application that plots F(x, y) = P(X > x | Y > y)
 * as a color-mapped surface, plus a wireframe overlay
 * for a classic 3D plot appearance.
 */
public class Main extends Application {

    private TextField tfMuX     = new TextField("0.0");
    private TextField tfMuY     = new TextField("0.0");
    private TextField tfSigmaX  = new TextField("1.0");
    private TextField tfSigmaY  = new TextField("1.0");
    private TextField tfRho     = new TextField("0.5");
    private TextField tfA       = new TextField("0.5");
    private TextField tfB       = new TextField("0.5");
    private Label     lblResult = new Label("F(a,b)=?");

    private Group plotGroup = new Group();

    private static final double X_MIN = -4.0;
    private static final double X_MAX =  4.0;
    private static final double Y_MIN = -4.0;
    private static final double Y_MAX =  4.0;
    private static final int    STEPS_X = 40;
    private static final int    STEPS_Y = 40;
    private static final double HEIGHT_SCALE = 2.0;
    private static final double TAIL_MULTIPLIER = 5.0;
    private static final int COLOR_MAP_SIZE = 256;
    private WritableImage colorMapImage;

    @Override
    public void start(Stage stage) {
        FlowPane topPane = new FlowPane(8,8);
        topPane.getChildren().addAll(
                new Label("muX:"), tfMuX,
                new Label("muY:"), tfMuY,
                new Label("sigmaX:"), tfSigmaX,
                new Label("sigmaY:"), tfSigmaY,
                new Label("rho:"),   tfRho,
                new Label("a:"),     tfA,
                new Label("b:"),     tfB
        );
        Button btnRefresh = new Button("Refresh");
        topPane.getChildren().addAll(btnRefresh, lblResult);
        btnRefresh.setOnAction(e -> refreshPlot());

        Group root3D = new Group(plotGroup);
        SceneAntialiasing aa = SceneAntialiasing.BALANCED;
        javafx.scene.SubScene subScene = new javafx.scene.SubScene(root3D, 800, 600, true, aa);

        PerspectiveCamera cam = new PerspectiveCamera(true);
        cam.setNearClip(0.1);
        cam.setFarClip(1000);
        cam.getTransforms().addAll(
                new Translate(0, -8, -12),
                new Rotate(-25, Rotate.X_AXIS)
        );
        subScene.setCamera(cam);

        AmbientLight amb = new AmbientLight(Color.color(0.8,0.8,0.8));
        root3D.getChildren().add(amb);
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(5);
        light.setTranslateY(-5);
        light.setTranslateZ(-8);
        root3D.getChildren().add(light);

        BorderPane root = new BorderPane();
        root.setTop(topPane);
        root.setCenter(subScene);
        Scene scene = new Scene(root, 1000, 700, true);
        stage.setScene(scene);
        stage.setTitle("3D Plot of P(X > x | Y > y) -- Color + Wireframe");
        stage.show();

        colorMapImage = buildColorMap(COLOR_MAP_SIZE);
        refreshPlot();
    }

    /**
     * Builds both the fill-surface and the wireframe-surface from the same geometry.
     * Then updates the label with F(a,b).
     */
    private void refreshPlot() {
        double muX = Double.parseDouble(tfMuX.getText());
        double muY = Double.parseDouble(tfMuY.getText());
        double sX  = Double.parseDouble(tfSigmaX.getText());
        double sY  = Double.parseDouble(tfSigmaY.getText());
        double rho = Double.parseDouble(tfRho.getText());
        double a   = Double.parseDouble(tfA.getText());
        double b   = Double.parseDouble(tfB.getText());

        TriangleMesh mesh = buildSurfaceMesh(muX, muY, sX, sY, rho);

        MeshView surfaceFill = new MeshView(mesh);
        PhongMaterial fillMat = new PhongMaterial();
        fillMat.setDiffuseMap(colorMapImage);
        surfaceFill.setMaterial(fillMat);
        surfaceFill.setDrawMode(DrawMode.FILL);
        surfaceFill.setCullFace(CullFace.NONE);

        MeshView surfaceWire = new MeshView(mesh);
        PhongMaterial wireMat = new PhongMaterial(Color.BLACK);
        surfaceWire.setMaterial(wireMat);
        surfaceWire.setDrawMode(DrawMode.LINE);
        surfaceWire.setCullFace(CullFace.NONE);

        plotGroup.getChildren().clear();
        plotGroup.getChildren().addAll(surfaceFill, surfaceWire);

        double val = conditionalTailProb(a, b, muX, muY, sX, sY, rho);
        lblResult.setText(String.format("F(a,b)=%.4f", val));
    }

    /**
     * Builds a TriangleMesh for the surface. Each vertex has:
     * (X, Z, Y) with Z = F(x,y)*HEIGHT_SCALE,
     * texture coords in (F, 0.5) so color depends on F in [0..1].
     */
    private TriangleMesh buildSurfaceMesh(double muX, double muY,
                                          double sigmaX, double sigmaY,
                                          double rho) {
        TriangleMesh mesh = new TriangleMesh();
        int countX = STEPS_X + 1;
        int countY = STEPS_Y + 1;
        double dx = (X_MAX - X_MIN) / STEPS_X;
        double dy = (Y_MAX - Y_MIN) / STEPS_Y;

        for (int iy=0; iy<countY; iy++){
            double yVal = Y_MIN + iy*dy;
            for (int ix=0; ix<countX; ix++){
                double xVal = X_MIN + ix*dx;
                float fVal = (float) conditionalTailProb(xVal, yVal, muX, muY, sigmaX, sigmaY, rho);
                if(fVal < 0) fVal=0;
                if(fVal > 1) fVal=1;
                float zPos = (float)(fVal * HEIGHT_SCALE);
                mesh.getPoints().addAll((float)xVal, zPos, (float)yVal);
                mesh.getTexCoords().addAll(fVal, 0.5f);
            }
        }
        for(int iy=0; iy<STEPS_Y; iy++){
            for(int ix=0; ix<STEPS_X; ix++){
                int p0 = iy*countX + ix;
                int p1 = p0 + 1;
                int p2 = p0 + countX;
                int p3 = p2 + 1;
                mesh.getFaces().addAll(p0,p0, p1,p1, p2,p2);
                mesh.getFaces().addAll(p1,p1, p3,p3, p2,p2);
            }
        }
        return mesh;
    }

    /**
     * Builds a 1D color gradient from purple (f=0) to yellow (f=1), 256x1.
     */
    private WritableImage buildColorMap(int size) {
        WritableImage img = new WritableImage(size,1);
        PixelWriter pw = img.getPixelWriter();
        for (int i=0; i<size; i++){
            double t = i/(double)(size-1);
            double r = 0.5 + 0.5*t;
            double g = 0.0 + 1.0*t;
            double b = 0.5 - 0.5*t;
            pw.setColor(i, 0, Color.color(r,g,b));
        }
        return img;
    }

    /**
     * Computes F(x, y) = P(X > x | Y > y).
     */
    private double conditionalTailProb(double x, double y,
                                       double muX, double muY,
                                       double sX, double sY,
                                       double rho) {
        double denom = tailProbabilityUnivariate(y, muY, sY);
        if(denom<1e-15) return 0.0;
        double numer = jointTailProbability(x, y, muX, muY, sX, sY, rho);
        return numer/denom;
    }

    /**
     * Tail probability for Y>y.
     */
    private double tailProbabilityUnivariate(double yLower, double mu, double sig){
        double yMax = mu + TAIL_MULTIPLIER*sig;
        if(yLower>=yMax) return 0.0;
        int N=200;
        double dy=(yMax-yLower)/N;
        double sum=0;
        for(int i=0; i<N; i++){
            double yy = yLower + (i+0.5)*dy;
            sum += univariatePdf(yy,mu,sig);
        }
        return sum*dy;
    }

    /**
     * Joint tail probability P(X>x, Y>y).
     */
    private double jointTailProbability(double xLower, double yLower,
                                        double muX, double muY,
                                        double sX, double sY,
                                        double rho) {
        double xMax = muX + TAIL_MULTIPLIER*sX;
        double yMax = muY + TAIL_MULTIPLIER*sY;
        if(xLower>=xMax||yLower>=yMax) return 0.0;
        int Nx=80, Ny=80;
        double dx=(xMax-xLower)/Nx;
        double dy=(yMax-yLower)/Ny;
        double sum=0;
        for(int i=0;i<Nx;i++){
            double xx = xLower + (i+0.5)*dx;
            for(int j=0;j<Ny;j++){
                double yy = yLower + (j+0.5)*dy;
                sum += bivariatePdf(xx,yy, muX,muY,sX,sY,rho);
            }
        }
        return sum*dx*dy;
    }

    /**
     * Univariate normal PDF.
     */
    private double univariatePdf(double x, double mu, double sig){
        double z=(x-mu)/sig;
        return Math.exp(-0.5*z*z)/(sig*Math.sqrt(2*Math.PI));
    }

    /**
     * Bivariate normal PDF.
     */
    private double bivariatePdf(double x, double y,
                                double mx, double my,
                                double sx, double sy,
                                double rho){
        double omr2=1-rho*rho;
        double zx=(x-mx)/sx;
        double zy=(y-my)/sy;
        double q=zx*zx+zy*zy-2*rho*zx*zy;
        double norm=1.0/(2*Math.PI*sx*sy*Math.sqrt(omr2));
        return norm*Math.exp(-0.5*q/omr2);
    }

    public static void main(String[] args) {
        launch(args);
    }
}