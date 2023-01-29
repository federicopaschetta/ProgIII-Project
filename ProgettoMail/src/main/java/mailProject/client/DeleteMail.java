package mailProject.client;

import mailProject.common.Email;
import mailProject.common.User;
import mailProject.common.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Class used to delete email, implements Callable as it's a task that needs to be called
 */
public class DeleteMail implements Callable<Boolean> {
    /**
     * Curernt User of service
     */
    private final User user;
    /**
     * Socket to interact with
     */
    private final Socket socket;
    /**
     * Email to be deleted
     */
    private final Email email;

    /**
     * @param user
     * @param email
     * @throws IOException
     * Constructor of class, creates a socket object with Util host and port
     */
    public DeleteMail(User user, Email email) throws IOException {
        this.user = user;
        this.email = email;
        this.socket = new Socket(Util.host, Util.mailPort);
    }

    /**
     * @return true if mail has been deleted, false otherwise
     * @throws Exception
     */
    @Override
    public Boolean call() throws Exception {
        try {
            String opString = "Delete";
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(user);
            out.writeUTF(opString);
            out.writeObject(email);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return in.readBoolean();
        } finally {
            socket.close();
        }
    }
}
