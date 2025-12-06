module com.uhi_visualizer {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires org.json;

    opens com.uhi_visualizer to javafx.fxml;
    exports com.uhi_visualizer;
}