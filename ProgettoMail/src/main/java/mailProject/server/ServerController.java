package mailProject.server;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller of Server application
 */
public class ServerController implements Initializable {
    /**
     * FXML Listview of logs in loglist
     */
    @FXML
    private ListView<String> logView;

    /**
     * Server model controller is hooked to
     */
    private Model model;

    /**
     * @param model server model to be initialized
     * Sets class variable model to model given as parameter
     */
    public void initModel(Model model) {this.model = model;}

    /**
     * @param log log operation string
     * Adds log parameter to model loglist
     */
    public void addLog(String log) {
        Platform.runLater(()-> model.addLog(log));
    }

    /**
     * @param url
     * @param resourceBundle
     * Sets logview controller variable to actual model loglist
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()->{
            logView.setItems(model.getLogList());
        });
    }
}
