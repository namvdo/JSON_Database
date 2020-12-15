package server;

import com.google.gson.JsonObject;

/**
 * @author namvdo
 * Utility class to represent commands and intial set up file location for client and server.
 */
public class CommandUtils {
    private CommandUtils() {

    }
    public static final String SEND = "Sent";
    public static final String RECEIVE = "Received";
    public static final String RESPONSE_OK = "OK";
    public static final String RESPONSE_ERROR = "ERROR";
    public static final String RESPONSE_ERROR_REASON = "No such key";
    public static final String RESPONSE = "response";
    public static final String VALUE = "value";
    public static final String REASON = "reason";
    public static final String CLIENT_DATA_FOLDER_LOCATION = "/Users/namvdo/Desktop/learntocodetogether.com/java-from-scratch/JSON Database/JSON Database/task/src/client/data/";
    public static final String SERVER_FILE_LOCATION = "/Users/namvdo/Desktop/learntocodetogether.com/java-from-scratch/JSON Database/JSON Database/task/src/server/data/data.json";
    public static final String TYPE = "type";
    public static final String KEY = "key";
    public static final String GET = "get";
    public static final String SET = "set";
    public static final String DELETE = "delete";
    public static final int PORT = 8080;
    public static final String ADDRESS = "localhost";

}
