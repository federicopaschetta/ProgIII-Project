package mailProject.common;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Class representing instance of an Email to be sent. Needs to be Serializable in order to be sent via Socket
 */
public class Email implements Serializable {
    /**
     * Identificator of Email
     */
    private String id;
    /**
     * Sender of Email
     */
    private String sender;
    /**
     * List of receivers of Email
     */
    private List<String> receivers;
    /**
     * Subject of Email
     */
    private String subject;
    /**
     * Main text of Email
     */
    private String text;
    /**
     * Date of creation of Email
     */
    private Date date;

    /**
     * @param sender
     * @param receivers
     * @param subject
     * @param text
     * @param date
     * Constructor of Email, creates a unique ID with Java inner method
     */
    public Email(String sender, List<String> receivers, String subject, String text, Date date) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.receivers = receivers;
        this.subject = subject;
        this.text = text;
        this.date = date;
    }

    /**
     * @return id of Email
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     * Sets id of Email to id given
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return sender of Email
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender
     * Sets sender of Email to sender given
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return list of String representing receivers of Email
     */
    public List<String> getReceivers() {
        return receivers;
    }

    /**
     * @param receivers
     * Sets receivers field of Email to parameter given
     */
    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    /**
     * @return subject of Email
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject
     * Sets subject of Email to subject given
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return main text of Email
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     * Sets text of Email to text given
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return date of Email creation
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date
     * Sets date of email to date given
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return string of Email with main infos
     */
    @Override
    public String toString() {
        return sender+" | "+date.toString().substring(0, 16)+" | "+subject+" ";
    }

    /**
     * @return string to be inserted in Email to highlight that Email is a reply
     */
    public String replyMessage() {
        return "\n\n\n------------------------------------------------------------------------------------------------\n" +
                "Il giorno " + getDate() + " l'utente " + getSender() + " ha scritto:\n\n" + getText();
    }

    /**
     * @return string to be inserted in Email to highlight that Email is a forward
     */
    public String forwardMessage() {
        return  "\n\n\n-------------------------------------Forwarded message------------------------------------------\n" +
                "Da: " + getSender() + "\nData: " + getDate() + "\nOggetto: " + getSubject() + "\nA: " + getReceivers() + "\n\n" + getText();
    }

    /**
     * @param o object to be compared with
     * @return true if o is Email class and has equal fields of this Email, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(id, email.id) && Objects.equals(sender, email.sender) &&
                Objects.equals(receivers, email.receivers) && Objects.equals(subject, email.subject)
                && Objects.equals(text, email.text) && Objects.equals(date, email.date);
    }
}
