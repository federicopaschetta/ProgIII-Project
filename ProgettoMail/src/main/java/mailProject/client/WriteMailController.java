package mailProject.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import mailProject.common.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Controller of mail writer view
 */
public class WriteMailController implements Initializable {

    /**
     * Field where receiver mail address is written
     */
    @FXML
    private TextField to;
    /**
     * Field where email subject is written
     */
    @FXML
    private TextField subject;
    /**
     * Field where mail text is written
     */
    @FXML
    private TextArea mailText;
    /**
     * Main panel
     */
    @FXML
    private SplitPane splitPane;

    /**
     * Inbox controller model
     */
    private Inbox inbox;
    /**
     * Mail sender
     */
    private String From;
    /**
     * Mail receiver/receivers
     */
    private String To;
    /**
     * Mail subject
     */
    private String Subject;
    /**
     * Mail text
     */
    private String Text;

    /**
     * @return receivers textfield
     */
    public TextField getTo() {
        return to;
    }

    /**
     * @return subject textfield
     */
    public TextField getSubject() {
        return subject;
    }

    /**
     * @return text textfield
     */
    public TextArea getText() {
        return mailText;
    }

    /**
     * sets receivers string to receivers textfield text
     */
    public void setTo() {
        this.To = to.getText();
    }

    /**
     * sets subject string to subject textfield text
     */
    public void setSubject() {
        this.Subject = subject.getText();
    }

    /**
     * sets text string to mail text textfield text
     */
    public void setText() {
        this.Text = mailText.getText();
    }

    /**
     * @param model model of controller
     * Sets controller model to model given
     */
    public void initModel(Inbox model) {
        this.inbox = model;
    }

    /**
     * @param url
     * @param resourceBundle
     * sets sender field in write mail view and initializes view
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            splitPane.lookupAll(".split-pane-divider").forEach(div -> div.setMouseTransparent(true));
            From = inbox.getUser().getEmail();
            mailText.setWrapText(true);
        });
    }

    /**
     * @param s mail address to be checked
     * @return true if s string matches mail address pattern
     */
    private boolean checkWrongEmail(String s) {
        Pattern pattern = Pattern.compile(Util.regex);
        if (s != null) {
            return (!pattern.matcher(s).matches());
        } else {
            return true;
        }
    }

    /**
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * Sets all email fields in Strings with FXML Nodes using class methods. Removes from receiverslist mail addresses
     * not matching pattern, display alerts differently if receivers list is empty or only some receivers don't match pattern.
     * Then checks in the same way subject and text of email, displaying alerts if there are troubles.
     * If everything is OK, controller invokes model method to send email with selected fields
     */
    public void send() throws IOException, InterruptedException, ExecutionException {
        this.setTo();
        ArrayList<String> toList = new ArrayList<>(Arrays.asList(To.split(", ")));
        int initReceiversNumber = toList.size();
        toList.removeIf(this::checkWrongEmail);
        if(this.To != null && !(this.To.equals("")) && toList.size()>0) {
            setSubject();
            setText();
            if(toList.size()<initReceiversNumber) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Attenzione!");
                alert.setHeaderText("Almeno un destinatario non valido");
                if (toList.size() == 0) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Nessuna mail rispetta il pattern");
                    alert.showAndWait();
                } else {
                    alert.setContentText("Solo " + toList + "rispetta il pattern");
                    alert.setContentText("Premi annulla per modificare gli indirizzi,\nse premi OK la mail verrà inviata solo a:\n" + toList.toString());
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() != ButtonType.OK) return;
                }
            }
            if(Subject == null || Subject.equals("")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Attenzione");
                alert.setHeaderText("Non hai specificato l'oggetto della mail.\nSe premi OK la mail verrà inviata senza oggetto");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) return;
            }
            if(Text == null || Text.equals("")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Attenzione");
                alert.setHeaderText("Non hai specificato il testo della mail.\nSe premi OK la mail verrà inviata senza testo");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) return;
            }
            inbox.sendEmail(From, toList, Subject, Text);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            if (toList.size() == 0) {
                alert.setContentText("Nessuna mail rispetta il pattern");
            }
            alert.setTitle("Destinatario mancante");
            alert.setHeaderText("Inserisci un destinatario per continuare");
            alert.showAndWait();
        }
    }

}
