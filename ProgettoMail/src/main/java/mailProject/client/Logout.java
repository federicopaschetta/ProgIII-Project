package mailProject.client;

import mailProject.common.User;
import mailProject.common.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Class used to log out User
 */
public class Logout implements Callable<Boolean> {
    /**
     * User logging out
     */
    private final User user;
    /**
     * Socket needed to interact with server
     */
    private final Socket socket;

    /**
     * @param user user to be assigned to user field
     * @throws IOException
     * Constructor of class, creates a new socket with Util host and port
     */
    public Logout(User user) throws IOException {
        this.user = user;
        this.socket = new Socket(Util.host, Util.mailPort);
    }

    /**
     * @return true if user succesfully logged out, false otherwise
     * @throws Exception
     * Method interacts with server and logs out from service
     */
    @Override
    public Boolean call() throws Exception {
        try {
            String opString = "Logout";
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(user);
            out.writeUTF(opString);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return in.readBoolean();
        } finally {
            socket.close();
        }
    }
}
