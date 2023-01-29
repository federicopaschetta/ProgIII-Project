package mailProject.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import mailProject.common.Email;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller of mail viewer view
 */
public class ViewMailController implements Initializable {

    /**
     * Main panel
     */
    @FXML
    private SplitPane splitPane;
    /**
     * Button used to reply mail to certain receivers
     */
    @FXML
    public Button reply;
    /**
     * Button used to reply mail to all receivers
     */
    @FXML
    public Button replyAll;
    /**
     * Button used to forward mail to other receivers
     */
    @FXML
    public Button forward;
    /**
     * Label of sender
     */
    @FXML
    public Label senderLabel;
    /**
     * Label of receivers
     */
    @FXML
    public Label receiverLabel;
    /**
     * Label of subject
     */
    @FXML
    public Label subjectLabel;
    /**
     * Label of date
     */
    @FXML
    public Label dateLabel;
    /**
     * Text of email
     */
    @FXML
    private TextArea textMessage;


    /**
     * Controller of main view
     */
    private MainController mainController;
    /**
     * Model of controller, inherited by mainController
     */
    private Inbox inbox;

    /**
     * Sender stringProperty
     */
    private final SimpleStringProperty sender = new SimpleStringProperty();
    /**
     * Receiver StringProperty
     */
    private final SimpleStringProperty receiver = new SimpleStringProperty();
    /**
     * Subject StringProperty
     */
    private final SimpleStringProperty subject = new SimpleStringProperty();
    /**
     * Date StringProperty
     */
    private final SimpleStringProperty date = new SimpleStringProperty();
    /**
     * Text StringProperty
     */
    private final SimpleStringProperty text = new SimpleStringProperty();

    /**
     * @param mainController controller of main view
     * Sets maincontroler to controller given
     */
    public void setController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * @param inbox model to give to controller
     * Sets inbox model field to inbox given
     */
    public void initModel(Inbox inbox) {
        this.inbox = inbox;
    }

    /**
     * @param url
     * @param resourceBundle
     * Binds all text properties to corrispective labels, then sets fields in order to view current email
     * and sets buttons visible
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(()-> {
            splitPane.lookupAll(".split-pane-divider").forEach(div -> div.setMouseTransparent(true));
            senderLabel.textProperty().bind(sender);
            receiverLabel.textProperty().bind(receiver);
            subjectLabel.textProperty().bind(subject);
            dateLabel.textProperty().bind(date);
            textMessage.textProperty().bind(text);
            if(inbox.getCurrentEmail() != null) {
                sender.set("Mittente: "+ inbox.getCurrentEmail().getSender());
                List<String> receiversList = inbox.getCurrentEmail().getReceivers();
                if(receiversList.size() == 1) {
                    receiver.set("Destinatario: "+receiversList.get(0));
                } else {
                    String receiversListString = String.join(", ", inbox.getCurrentEmail().getReceivers());
                    receiver.set("Destinatari: "+receiversListString);
                }
                subject.set("Oggetto: "+ inbox.getCurrentEmail().getSubject());
                date.set("Data: "+ inbox.getCurrentEmail().getDate());
                text.set(inbox.getCurrentEmail().getText());
                reply.setVisible(true);
                forward.setVisible(true);
                replyAll.setVisible(receiversList.size()>1);
            }
        });
    }

    /**
     * Method invoked when button reply is pressed, loads write mail view, hooks controller to view,
     * initializes model and email fields to model current email fields
     */
    public void reply() {
        if(inbox.getCurrentEmail() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewClient/WriteMail.fxml"));
                Parent writeMail = loader.load();
                mainController.getBorderPane().setCenter(writeMail);
                WriteMailController writeMailController = loader.getController();
                writeMailController.initModel(inbox);
                writeMailController.getTo().setText(inbox.getCurrentEmail().getSender());
                writeMailController.getSubject().setText(inbox.getCurrentEmail().getSubject());
                writeMailController.getText().setText(inbox.getCurrentEmail().replyMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method invoked when button forward is pressed, loads write mail view, hooks controller to view,
     * initializes model and email fields to model current email fields
     */
    public void forward() {
        if(inbox.getCurrentEmail() != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewClient/WriteMail.fxml"));
                Parent writeMail = loader.load();
                mainController.getBorderPane().setCenter(writeMail);
                WriteMailController writeMailController = loader.getController();
                writeMailController.initModel(inbox);
                writeMailController.getSubject().setText(inbox.getCurrentEmail().getSubject());
                writeMailController.getText().setText(inbox.getCurrentEmail().forwardMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Method invoked when button reply to all is pressed, resets current email in model,
     * loads write mail view, hooks controller to view,
     * initializes model and email fields to model current email fields, except for receivers list,
     * where removes own mail address and adds former sender mail address
     */
    public void replyAll() {
        inbox.setCurrentEmail(null);
        for (Email e : inbox.getUser().getReceivedEmails()) {
            if (mainController.mailList.getSelectionModel().getSelectedItem().equals(e)) {
                inbox.setCurrentEmail(e);
            }
        }
        if(inbox.getCurrentEmail() == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewClient/WriteMail.fxml"));
            Parent writeMail = loader.load();
            mainController.getBorderPane().setCenter(writeMail);
            WriteMailController writeMailController = loader.getController();
            writeMailController.initModel(inbox);
            ArrayList<String> receiversList = (ArrayList<String>) inbox.getCurrentEmail().getReceivers();
            receiversList.remove(inbox.getUser().getEmail());
            receiversList.add(inbox.getCurrentEmail().getSender());
            String receiversString = String.join(", ", receiversList);
            writeMailController.getTo().setText(receiversString);
            writeMailController.getSubject().setText(inbox.getCurrentEmail().getSubject());
            writeMailController.getText().setText(inbox.getCurrentEmail().replyMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
