package mailProject.server;

import mailProject.common.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Thread handling login request from client
 */
public class LoginHandler extends Thread {
    /**
     * Server socket to interface with client
     */
    private final Socket socket;
    /**
     * Server Controller to be hooked with
     */
    private final ServerController serverController;

    /**
     * @param socket socket to be used
     * @param serverController server controller handling request
     * Class constructor, sets class variables to parameters given
     */
    public LoginHandler(Socket socket, ServerController serverController) {
        this.socket = socket;
        this.serverController = serverController;
    }

    /**
     * Method needed by thread interface, conducts mainly the request,
     * gets user from client input, if not exists, it's created in local, otherwise gets his info from takeUser,
     * sends it back and add the event to controller loglist.
     */
    public void run() {
        ObjectInputStream in;
        ObjectOutputStream out;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            Object obj = in.readObject();
            if(obj instanceof User) {
                User user = (User) obj;
                if(!(existsAccount(user))) {
                    createAccount(user);
                }
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(takeUser(user));
                serverController.addLog(new Date()+": "+ user.getEmail()+" ha effettuato l'accesso.\n");
                out.flush();
                out.close();
                in.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param user user to be checked
     * @return true if accounts already exists, false otherwise
     * @throws IOException
     * Method looks in local if accountlist exists, if not create directory and account file in it and return false.
     * If account list is empty returns false, otherwise it looks in file if it's contained user, if it is, it returns true.
     * Then it closes all streams.
     */
    private synchronized boolean existsAccount(User user) throws IOException{
        File accounts = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Users" + File.separator + "usersFile.txt");
        if (!(accounts.exists())) {
            accounts.getParentFile().mkdirs();
            accounts.createNewFile();
            return false;
        }
        List<User> userList;
        try {
            FileInputStream in = new FileInputStream(accounts.getAbsolutePath());
            if (in.available() == 0) {
                return false;
            }
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            userList = (List<User>) objectInputStream.readObject();
            if (userList.contains(user)) {
                return true;
            }
            objectInputStream.close();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param user user whose account need to be created
     * Method adds user to userlist, updates userlist in its file, creates user own folder and adds log to controller loglist
     */
    public synchronized void createAccount(User user) {
        List<User> userList = new ArrayList<>();
        try {
            File accounts = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Users" + File.separator + "usersFile.txt");
            FileInputStream in = new FileInputStream(accounts);
            if (!accounts.exists() || in.available() == 0) {
                accounts.getParentFile().mkdirs();
                accounts.createNewFile();
                userList.add(user);
                FileOutputStream out = new FileOutputStream(accounts);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                objectOutputStream.writeObject(userList);
                out.close();
                objectOutputStream.close();
            } else {
                ObjectInputStream objectInputStream = new ObjectInputStream(in);
                List<User> getList = (List<User>) objectInputStream.readObject();
                getList.add(user);
                objectInputStream.close();
                FileOutputStream out = new FileOutputStream(accounts);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                objectOutputStream.writeObject(getList);
                out.close();
                objectOutputStream.close();
            }
            makeFolder(user);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        serverController.addLog(new Date()+": E' stato creato un nuovo account con mail: "+user.getEmail());
    }

    /**
     * @param user user whose folder needs to be created
     * @throws IOException
     * Method checks if user folder and file exist, if they don't, they are created and info are written inside it.
     * At the end, all streams are closed.
     */
    private void makeFolder(User user) throws IOException{
        File file = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Utenti" + File.separator + user.getEmail() + File.separator + "utente.txt");
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
        objectOutputStream.writeObject(user);
        out.close();
        objectOutputStream.close();
    }

    /**
     * @param utente user whose info need to be taken
     * @return user with all info updated
     * Method reads user object from local file and returns it.
     */
    private synchronized User takeUser(User utente) {
        try {
            File file = new File(Paths.get(System.getProperty("user.dir")) + File.separator + "Utenti" + File.separator + utente.getEmail() + File.separator + "utente.txt");
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(in);
            return (User) objectInputStream.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
