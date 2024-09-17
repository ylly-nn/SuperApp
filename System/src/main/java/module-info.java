module org.example.kr {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.compiler;
    requires java.desktop;

    opens org.kr to javafx.fxml;
    exports org.kr;
    exports org.kr.controllers;
    opens org.kr.controllers to javafx.fxml;
}