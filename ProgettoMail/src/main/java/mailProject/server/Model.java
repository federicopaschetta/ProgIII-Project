package mailProject.server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Server model class
 */
public class Model {
    /**
     * List of String representing all actions handled by server
     */
    private ObservableList<String> logList;

    /**
     * Constructor of class, creates a new observable array list and passes it to loglist variable
     */
    public Model() {
        this.logList = FXCollections.observableArrayList();
    }

    /**
     * @return list of server logs
     */
    public ObservableList<String> getLogList() {
        return logList;
    }

    /**
     * @param log log operation to be added
     * Adds log given to logList
     */
    public void addLog(String log) {
        this.logList.add(log);
    }
}
