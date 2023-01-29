package mailProject.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import mailProject.common.Email;
import mailProject.common.User;
import mailProject.common.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Class representing client model
 */
public class Inbox {
    /**
     * User of mail service
     */
    private User user;
    /**
     * List of mails received by user
     */
    private final ObservableList<Email> mailList;
    /**
     * Selected email (to be viewed or deleted)
     */
    private Email currentEmail;
    /**
     *
     */
    public boolean alertOneTime = false;

    /**
     * Constructor of model, creates an observable arraylist
     */
    public Inbox() {
        this.mailList = FXCollections.observableArrayList();
    }

    /**
     * @return user of mail service
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user User to be set
     * Sets user field to user given as parameter
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return received mail list
     */
    public ObservableList<Email> getMailList() {
        return mailList;
    }

    /**
     * Sets mail list to list of received mails from user,
     * then reverse the list, in order to have newer ones at the beginning
     */
    public void setMailList() {
        mailList.addAll(user.getReceivedEmails());
        FXCollections.reverse(mailList);
    }

    /**
     * @return current email highlighted
     */
    public Email getCurrentEmail() {
        return currentEmail;
    }

    /**
     * @param currentEmail email to be set
     * Sets currentEmail field to email given as parameter
     */
    public void setCurrentEmail(Email currentEmail) {
        this.currentEmail = currentEmail;
    }

    /**
     * @param user user to be logged in
     * @throws ExecutionException
     * @throws InterruptedException
     * Method executes check connection task, if this one goes in the right way,
     * then it gets the user with updated attributes from the server, otherwise displays an error
     */
    public void login(User user) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> ft = new FutureTask<>(new CheckConnection(Util.host, Util.loginPort));
        executorService.execute(ft);
        if(ft.get()) {
            try {
                Socket socket = new Socket(Util.host, Util.loginPort);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(user);
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                user = (User) in.readObject();
                out.close();
                in.close();
                setUser(user);
            } catch(IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server Offline");
            alert.setHeaderText("Impossibile connettersi al server!");
            alert.setContentText("Sembra che il server sia offline, riprova più tardi");
            alert.showAndWait();
        }
        executorService.shutdown();
    }

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * Method at first checks connnection from server, if connection is OK, it executes logout task
     */
    public void logout() throws ExecutionException, InterruptedException, IOException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> ft = new FutureTask<>(new CheckConnection(Util.host, Util.mailPort));
        executorService.execute(ft);
        if (ft.get()) {
            ExecutorService executorService1 = Executors.newSingleThreadExecutor();
            FutureTask<Boolean> ft1 = new FutureTask<>(new Logout(user));
            executorService1.execute(ft1);
        }
        executorService.shutdown();
    }

    /**
     * @param from sender of email
     * @param to list of email receivers
     * @param subject subject of email
     * @param text text of email
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * Creates mail and calls task needed to send email
     * Method creates new email object with parameters given, then check connection, if connection is OK,
     * executes sendMail task. If sendMail response is null, email is sent to receiver, otherwise an error alert
     * is displayed highlighting accounts mail has not been sent to.
     */
    public void sendEmail(String from, List<String> to, String subject, String text) throws ExecutionException, InterruptedException, IOException {
        Email email = new Email(from, to, subject, text, new Date());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> ft = new FutureTask<>(new CheckConnection(Util.host, Util.mailPort));
        executorService.execute(ft);
        if(ft.get()) {
            executorService = Executors.newSingleThreadExecutor();
            FutureTask<ArrayList<String>> ft2 = new FutureTask<>(new SendMail(email, user));
            executorService.execute(ft2);
            ArrayList<String> response = ft2.get();
            if(response != null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Destinatario/i inesistente/i");
                alert.setHeaderText("Impossibile inviare E-Mail ad account inesistenti");
                alert.setContentText("La mail non è stata inviata a: " + response);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Email inviata correttamente");
                alert.setHeaderText("Operazione eseguita con successo!");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() != ButtonType.OK) return;
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server Offline");
            alert.setHeaderText("Impossibile connettersi al server!");
            alert.setContentText("Sembra che il server sia offline, riprova più tardi");
            alert.showAndWait();
        }
        executorService.shutdown();
    }

    /**
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws IOException
     * Calls task needed to delete mail from inbox
     * Method removes current mail from maillist, then checks connection, if it's OK,
     * it's executed deleteMail task with current user and current email. If task returns true,
     * mail has been deleted, otherwise it's displayed error alert.
     */
    public void delete() throws ExecutionException, InterruptedException, IOException {
        mailList.remove(getCurrentEmail());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        FutureTask<Boolean> ft = new FutureTask<>(new CheckConnection(Util.host, Util.mailPort));
        executorService.execute(ft);
        if(ft.get()) {
            executorService = Executors.newSingleThreadExecutor();
            FutureTask<Boolean> ft2 = new FutureTask<>(new DeleteMail(user, getCurrentEmail()));
            executorService.execute(ft2);
            if(ft2.get()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("E-Mail Cancellata");
                alert.setHeaderText("L'E-Mail è stata cancellata correttamente!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("L'email non è stata eliminata correttamente");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Server Offline");
            alert.setHeaderText("Impossibile connettersi al server!");
            alert.setContentText("Il server è offline, riprova più tardi");
            alert.showAndWait();
        }
        executorService.shutdown();
    }

    /**
     * Method keeps the client updated with all news related to mail account. Everything is handled by scheduler,
     * which repeats commands each 5 seconds, in order to get any news as soon as possible.
     * Runner executes more tasks one next to each other, at first checks connection, if it's OK checks if connection flag is true,
     * if it does, alerts the user that server is now online, then executes DataUser task and sets user with data got, in order to get
     * always user updated. When the user is updated, so is his mail list, at that moment it's checked if old maillist is equal
     * to user updated one, if the new one has more mails than old one, it means that a new mail has arrived, it's displayed the alert
     * and displays updated mail list.
     * At the beginning, if connection is not OK, it's displayed an alert to user and flag is set to true, and connection will
     * be checked again in 5 seconds.
     */
    public void refresh() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        final Runnable runner = ()->{
            try {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                FutureTask<Boolean> ft = new FutureTask<>(new CheckConnection(Util.host, Util.mailPort));
                executorService.execute(ft);
                if(ft.get()) {
                    if(alertOneTime) {
                        Platform.runLater(()->{
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Server Online");
                            alert.setHeaderText("Il server ora è online!");
                            alert.showAndWait();
                        });
                        alertOneTime = false;
                    }
                    ExecutorService executorService1 = Executors.newSingleThreadExecutor();
                    FutureTask<User> ft2 = new FutureTask<>(new DataUser(user));
                    executorService1.execute(ft2);
                    User user = ft2.get();
                    executorService1.shutdown();
                    System.out.println("Ho aggiornato "+user.getEmail());
                    if(!(user.equals(this.user))) {
                        setUser(user);
                        if(mailList.size()<user.getReceivedEmails().size()) {
                            Platform.runLater(()->{
                                mailList.clear();
                                mailList.addAll(user.getReceivedEmails());
                                FXCollections.reverse(mailList);
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Nuova Mail");
                                alert.setHeaderText("Hai ricevuto una nuova mail!");
                                alert.showAndWait();
                            });
                        }
                    }
                } else {
                    if(!alertOneTime) {
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Server Offline");
                            alert.setHeaderText("Impossibile connettersi al server!");
                            alert.setContentText("Il server è offline, riprova più tardi");
                            alert.showAndWait();
                        });
                        alertOneTime = true;
                    } else {
                        System.out.println("Server ancora offline");
                    }
                    executorService.shutdown();
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(runner, 0, 5, TimeUnit.SECONDS);
    }
}
