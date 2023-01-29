package mailProject.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing User that logs in the service. Implements Serializable in order to stream objects.
 */
public class User implements Serializable {
    /**
     * email address of User, identifier of him
     */
    private final String email;
    /**
     * list of inbox emails received by user
     */
    private final List<Email> receivedEmails;

    /**
     * @param email
     * Constructor of User, creates an empty ArrayList
     */
    public User(String email) {
        this.email = email;
        receivedEmails = new ArrayList<>();
    }

    /**
     * @return email address of User
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return list of email received by User
     */
    public List<Email> getReceivedEmails() {
        return receivedEmails;
    }

    /**
     * @param email
     * adds email given to list of received emails
     */
    public void addReceivedEmail(Email email) {
        receivedEmails.add(email);
    }

    /**
     * @param email
     * deletes email given from received emails
     */
    public void deleteReceivedEmail(Email email) {
        this.receivedEmails.removeIf(email1 -> email1.equals(email));
    }

    /**
     * @param obj
     * @return true if obj given belongs to User class and has equal fields of this, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && this.getClass().equals(obj.getClass())) {
            User user = (User) obj;
            return this.email.equals(user.getEmail()) &&
                    this.receivedEmails.equals(user.getReceivedEmails());
        }
        return false;
    }
}
