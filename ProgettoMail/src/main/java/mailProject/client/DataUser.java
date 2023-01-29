package mailProject.client;

import mailProject.common.User;
import mailProject.common.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 *
 */
public class DataUser implements Callable<User> {
    /**
     * Current user of service
     */
    private final User user;
    /**
     * Socket to interact with server
     */
    private final Socket socket;

    /**
     * @param user
     * @throws IOException
     * Constructor of class, creates an instance of socket with host and port from Util class
     */
    public DataUser(User user) throws IOException {
        this.user = user;
        this.socket = new Socket(Util.host, Util.mailPort);
    }

    /**
     * @return User object got from server, in order to have always it updated
     * @throws Exception
     */
    @Override
    public User call() throws Exception {
        try {
            String userString = "User";
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(user);
            out.writeUTF(userString);
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return (User) in.readObject();
        } finally {
            socket.close();
        }
    }
}
