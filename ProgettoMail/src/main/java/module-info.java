module mailProject {
    requires javafx.controls;
    requires javafx.fxml;


    opens mailProject.server to javafx.fxml;
    exports mailProject.server;

    opens mailProject.client to javafx.fxml;
    exports mailProject.client;

    opens mailProject.common to javafx.fxml;
    exports mailProject.common;
}