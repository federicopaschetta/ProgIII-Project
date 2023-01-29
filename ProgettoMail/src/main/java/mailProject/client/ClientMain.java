package mailProject.client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Main class, needed to start client project
 */
public class ClientMain extends Application {
    /**
     * @param primaryStage
     * @throws IOException
     * Methods called by launch, initializes and loads loader of view, hooks controller to view and then sets and shows scene
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/viewClient/login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.initModel(new Inbox());
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 600, 300));
        primaryStage.getScene().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/viewClient/style.css")).toExternalForm());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * @param args
     * Initial method that starts client, invokes start method
     */
    public static void main(String[] args) {
            launch(args);
        }
}
