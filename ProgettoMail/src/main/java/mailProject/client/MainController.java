package mailProject.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import mailProject.common.Email;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * Controller of main page of client
 */
public class MainController implements Initializable {

    /**
     * Main panel of view
     */
    @FXML
    public BorderPane borderPane;
    /**
     * Button used to delete mail
     */
    @FXML
    public Button delete;
    /**
     * Header of page
     */
    @FXML
    public Label header;
    /**
     * Client model
     */
    public Inbox inbox;
    /**
     * List of mails received by user
     */
    @FXML
    public ListView<Email> mailList;

    /**
     * Box with emails received by user
     */
    @FXML
    public AnchorPane receivedBox;


    /**
     * @param url
     * @param resourceBundle
     * Initializes class filling maillist, setting header text and invoking refresh method, in order to update list of mail
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()->{
            mailList.setItems(inbox.getMailList());
            inbox.setMailList();
            header.setText(inbox.getUser().getEmail());
            inbox.refresh();
        });
    }

    /**
     * @param inbox model to be initialized with
     * sets model field to model given
     */
    public void initModel(Inbox inbox) {
        this.inbox = inbox;
    }

    /**
     * @return border pane panel of view
     */
    public BorderPane getBorderPane() {
        return borderPane;
    }

    /**
     * Method invoked by write mail button directly in FXML, loads new view, hooks controller to view and initializes
     * controller field inherited by this controller, used to write a new mail
     */
    public void newMail() {
        try {
            receivedBox.setMinWidth(0.0);
            delete.setDisable(true);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewClient/WriteMail.fxml"));
            Parent newMail = loader.load();
            borderPane.setCenter(newMail);
            WriteMailController writeMailController = loader.getController();
            writeMailController.initModel(inbox);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method invoked by FXML directly when pressed on a mail, in order to view it.
     * It minimizes box to side view, gets selected mail, loads and sets new view, hooks controller to view
     * and initializes controller model with inherited model
     */
    public void printReceived() {
        receivedBox.setMinWidth(0.0);
        Email currentEmail = mailList.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewClient/ViewMail.fxml"));
            Parent viewmail = loader.load();
            borderPane.setCenter(viewmail);
            ViewMailController viewMailController = loader.getController();
            viewMailController.initModel(inbox);
            if(currentEmail != null) {
                inbox.setCurrentEmail(currentEmail);
                viewMailController.setController(this);
                delete.setDisable(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * Method invoked by FXML when button delete pressed, gets email received and invokes model method to delete mail
     */
    public void deleteMail() throws IOException, ExecutionException, InterruptedException {
        Email currentEmail = mailList.getSelectionModel().getSelectedItem();
        if(currentEmail != null) {
            inbox.setCurrentEmail(currentEmail);
            inbox.delete();
        }
        delete.setDisable(true);
    }

    /**
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     * Method invoked by FXML when logout button is pressed, invokes model logout method and then closes application
     */
    public void logout() throws IOException, ExecutionException, InterruptedException {
        inbox.logout();
        Platform.exit();
        System.exit(0);
    }

}
