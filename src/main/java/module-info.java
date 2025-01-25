module BivariantNormalPlotter {
    requires javafx.controls;
    requires javafx.graphics;
    requires org.jfree.jfreechart;
    requires javafx.fxml;
    requires java.desktop;

    exports deep.ui to javafx.graphics;
}