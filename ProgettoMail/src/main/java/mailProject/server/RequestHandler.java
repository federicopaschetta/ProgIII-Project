package mailProject.server;

import mailProject.common.Email;
import mailProject.common.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class handling life cycle mail application requests
 */
public class RequestHandler extends Thread{
    /**
     * Server socket where connection is done
     */
    private final Socket socket;
    /**
     * Server Controller that handles all server operations
     */
    private final ServerController serverController;
    /**
     * User logged in
     */
    private User user;

    /**
     * @param socket socket to be hooked
     * @param serverController server controller handling operations
     * Class constructor, associates method parameters to class variables
     */
    public RequestHandler(Socket socket, ServerController serverController) {
        this.socket = socket;
        this.serverController = serverController;
    }

    /**
     * @param user user to be set
     * Sets user class variable to parameter given
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Method run after thread is created, it has all operations user can do.
     * At first sets user read from input stream, then controls on the opString written in stream by client,
     * representing the operation that need to be done. Based on this control, invokes the correct private class method
     * performing the operation.
     */
    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Object userClient = in.readObject();
            if(userClient instanceof User tempUser) {
                setUser(tempUser);
                String opString = in.readUTF();
                switch (opString) {
                    case "Send" -> {
                        Object mailClient = in.readObject();
                        if (mailClient instanceof Email email) {
                            send(email);
                        }
                        break;
                    }
                    case "Delete" -> {
                        Object mailClient = in.readObject();
                        if (mailClient instanceof Email email) {
                            delete(email);
                        }
                        break;
                    }
                    case "User" -> {
                        userData();
                        break;
                    }

                    case "Logout" -> {
                        logout();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param email email need to be sent
     * @throws IOException
     * @throws ClassNotFoundException
     * Method sends email given as parameter to email receivers.
     * At first, receivers in receiverslist are divided in two groups, accepted and refused, if file representing user exists or not.
     * For each refused, its string is removed from receivers list.
     * For eache accepted, accepted receiver local file is taken, read, mail is added to his mail list and
     * user object is rewritten in the same place, doing it, user files are always updated.
     * Then stream are closed, successful deliver log is added to controller and eventual deliver refused log is added and refused receivers list
     * is written out to stream.
     */
    private synchronized void send(Email email) throws IOException, ClassNotFoundException {
        ArrayList<String> receivers = (ArrayList<String>) email.getReceivers();
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        ArrayList<String> accepted = new ArrayList<>();
        ArrayList<String> refused = new ArrayList<>();
        for(String receiver: receivers) {
            File file = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Utenti" + File.separator + receiver);
            if(file.exists()) {
                accepted.add(receiver);
            } else {
                refused.add(receiver);
            }
        }
        for (String refusedReceiver: refused) {
            receivers.remove(refusedReceiver);
        }
        email.setReceivers(accepted);
        for(String acceptedReceiver: accepted) {
            File file = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Utenti" + File.separator + acceptedReceiver + File.separator + "utente.txt");
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            User user = (User) objectInputStream.readObject();
            user.addReceivedEmail(email);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(user);
            objectInputStream.close();
            objectOutputStream.close();
            fileOutputStream.close();
            serverController.addLog(new Date()+": "+this.user.getEmail()+" ha inviato una mail a "+acceptedReceiver);
        }
        if(!refused.isEmpty()) {
            serverController.addLog(new Date()+": "+this.user.getEmail()+" non ha inviato una mail ai seguenti account perche' inesistenti: "+refused);
        }
        out.writeObject(refused);
        out.close();
    }

    /**
     * @throws IOException
     * @throws ClassNotFoundException
     * Method gets updated user data, reading user object from file stream and writing it out to stream, which will be read by client.
     */
    private synchronized void userData() throws IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        File file = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Utenti" + File.separator + user.getEmail() + File.separator + "utente.txt");
        FileInputStream in = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        User user = (User) objectInputStream.readObject();
        out.writeObject(user);
        in.close();
        objectInputStream.close();
        out.close();
    }

    /**
     * @param email email to be deleted
     * @throws IOException
     * @throws ClassNotFoundException
     * Method gets userdata from local file, invokes user method to delete given email,
     * overwrite file with output stream, returns true and closes all streams. At the end, it adds the log to servercontroller.
     */
    private synchronized void delete(Email email) throws IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        File file = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Utenti" + File.separator + user.getEmail() + File.separator + "utente.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fileInputStream);
        User user = (User) in.readObject();
        user.deleteReceivedEmail(email);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(user);
        in.close();
        objectOutputStream.close();
        fileOutputStream.close();
        out.writeBoolean(true);
        out.close();
        serverController.addLog(new Date() + ": " + user.getEmail() + " ha eliminato una mail");
    }


    /**
     * Method logs out user, writing log in controller loglist.
     */
    private void logout() {
        serverController.addLog(new Date() + ": " + user.getEmail() + " ha effettuato il logout");
    }

}
