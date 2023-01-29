package mailProject.server;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mailProject.common.Util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Server main class, used to launch server application
 */
public class ServerMain extends Application {
    /**
     * Server Controller hooked to main application
     */
    private ServerController serverController;

    /**
     * @param primaryStage stage to be set initially
     * Method invoked by launch at the beginning of the application
     * Loads view and sets it in primary stage, hooks controller to view, sets application primary logic
     * (closure logic), then shows the view.
     * Then create a new Thread, creates a server socket object,
     * adds its log in controller and starts an endless loop waiting for client request
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(ServerMain.class.getResource("/viewServer/logServer.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Server Log Console");
            serverController = loader.getController();
            serverController.initModel(new Model());
            primaryStage.setOnCloseRequest(windowEvent -> {
                Platform.exit();
                System.exit(0);
            });
            primaryStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(Util.loginPort);
                serverController.addLog(new Date() + ": Server avviato\n");
                while (true) {
                    Socket socket = serverSocket.accept();
                    Thread con = new LoginHandler(socket, serverController);
                    con.start();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(Util.mailPort);
                serverController.addLog(new Date() + ": In attesa di client\n");
                while (true) {
                    Socket socket = serverSocket.accept();
                    Thread connection = new RequestHandler(socket, serverController);
                    connection.start();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * @param args
     * Main method run by application, invoke immediately start method
     */
    public static void main(String[] args) {
        launch(args);
    }
}
