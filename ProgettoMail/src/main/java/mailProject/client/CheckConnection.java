package mailProject.client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;

/**
 * Class used to check valid connection with server, implements Callable interface as it's a task
 */
public class CheckConnection implements Callable<Boolean> {
    /**
     * Address of connection socket
     */
    private final SocketAddress socketAddress;
    /**
     * Socket object needed for check, already created
     */
    private final Socket socket = new Socket();

    /**
     * @param hostname
     * @param port
     * Constructor of class, needs to initialize only address, as socket is already there.
     * Uses method to create socketAddress given hostname and port
     */
    public CheckConnection(String hostname, int port) {
        socketAddress = new InetSocketAddress(hostname, port);
    }

    /**
     * @return true if server is up, false if it's down
     * Connects socket to address, gets Socket output stream, check if it's on and then closes everything
     */
    @Override
    public Boolean call() {
        try {
            socket.connect(socketAddress);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject("Test Connessione");
            socket.close();
            System.out.println("Server è online all indirizzo " + socketAddress);
            return true;
        }
        catch (IOException e) {
            System.out.println("Il server è offline!");
        }
        return false;
    }
}
