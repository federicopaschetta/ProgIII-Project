package mailProject.common;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

/**
 * Class with utilities for the project
 */
public class Util {
    /**
     * Address of host
     */
    public static String host = "localhost";

    /**
     * Mail port, where emails will be sent and received
     */
    public static int mailPort = 8189;
    /**
     * Port where login will be accepted or refused
     */
    public static int loginPort = 8001;
    /**
     * Regular expressions to check if email address belongs to classic email pattern
     */
    public static String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
}