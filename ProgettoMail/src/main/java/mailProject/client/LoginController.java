package mailProject.client;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import mailProject.common.User;
import mailProject.common.Util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * Controller of login view, needed to log in User
 */
public class LoginController implements Initializable {

    /**
     * Icon of mail shown in the view
     */
    @FXML
    private ImageView imageViewMail;
    /**
     * Field where mail address will be inserted
     */
    @FXML
    private TextField mailaddress;
    /**
     * Button that fires login procedure
     */
    @FXML
    private Button login;

    /**
     * String property that stores mail address from TextField
     */
    private SimpleStringProperty mail;
    /**
     * Model of client that handles everything
     */
    private Inbox inbox;

    /**
     * @param location
     * @param resources
     * Method invoked at the creation of controller, sets icon in view, binds StringProperty to TextField
     * and sets logic to login button (if enter button pressed, fires login method)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File iconMail = new File("src/main/resources/img/IconaMail.png");
        Image imageMail = new Image(iconMail.toURI().toString());
        imageViewMail.setImage(imageMail);
        imageViewMail.setPreserveRatio(true);
        imageViewMail.setFitHeight(85.0);
        mail = new SimpleStringProperty();
        mail.bind(mailaddress.textProperty());
        mailaddress.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.ENTER)) {
                login.fire();
            }
        });
    }

    /**
     * @param model model to be assigned to class field
     * Initializes model of controller with model given
     */
    public void initModel(Inbox model) {
        this.inbox = model;
    }

    /**
     * Checks if mail address given belongs to regex pattern or not. If it does, delegates login to model
     * and sets main page view, stylesheet and hooks controller to view. If mail doesn't match pattern or is null,
     * alerts the User and let him recompile textfield.
     */
    public void checkEmail(/*ActionEvent ae*/) {
        Pattern pattern = Pattern.compile(Util.regex);
        if(mail!=null) {
            if(pattern.matcher(mail.getValue()).matches()) {
                try {
                    User user = new User(mail.getValue());
                    Stage stage = (Stage)login.getScene().getWindow();
                    stage.close();
                    inbox.setUser(user);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/viewClient/ClientMain.fxml"));
                    Parent root = loader.load();
                    Stage primaryStage = new Stage();
                    primaryStage.setTitle("Mail");
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/viewClient/style.css")).toExternalForm());
                    primaryStage.setResizable(false);
                    primaryStage.setScene(scene);
                    inbox.login(user);
                    MainController mainController = loader.getController();
                    mainController.initModel(inbox);
                    primaryStage.setOnCloseRequest(windowEvent -> {
                        try {
                            mainController.logout();
                        } catch (IOException | ExecutionException | InterruptedException e ) {
                            e.printStackTrace();
                        }
                        Platform.exit();
                        System.exit(0);
                    });
                    primaryStage.show();
                } catch (IOException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Indirizzo E-Mail errato");
                alert.setHeaderText("L'E-Mail inserita non è corretta!");
                alert.setContentText("Sembra che tu stia cercando di inserire un'e-mail non valida");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Indirizzo E-Mail errato");
            alert.setHeaderText("L'E-Mail inserita non è corretta!");
            alert.setContentText("Sembra che tu stia cercando di inserire un'e-mail non valida");
            alert.showAndWait();
        }
    }
}
