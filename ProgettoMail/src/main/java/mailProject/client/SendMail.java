package mailProject.client;

import mailProject.common.Email;
import mailProject.common.User;
import mailProject.common.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Class used to send email to server, implements Callable as it's a task
 */
public class SendMail implements Callable<ArrayList<String>> {
    /**
     * Socket used to interact with server
     */
    private final Socket socket;
    /**
     * Email to send
     */
    private final Email email;
    /**
     * User sender of email
     */
    private final User user;

    /**
     * @param email email to be sent
     * @param user sender of email
     * @throws IOException
     * Constructor of class, creates a new socket object with host and port from util
     */
    public SendMail(Email email, User user) throws IOException {
        this.email = email;
        this.user = user;
        this.socket = new Socket(Util.host, Util.mailPort);
    }

    /**
     * @return list of receivers refused by server
     * @throws Exception
     * Interacts with server at socket, writes user and mail to server
     */
    @Override
    public ArrayList<String> call() throws Exception {
        try {
            String opString = "Send";
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(user);
            out.writeUTF(opString);
            out.writeObject(email);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> response = (ArrayList<String>) in.readObject();
            System.out.println(response);
            if(!response.isEmpty()) {
                return response;
            } else {
                return null;
            }
        } finally {
            socket.close();
        }
    }
}
