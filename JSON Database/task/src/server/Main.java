package server;

import client.CommandUtils;
import client.JsonObject;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author namvdo
 */
public class Main {
    private static final int PORT = 8080;
    private static final String ADDRESS = "localhost";
    private static void openSocket(ServerSocket serverSocket, JsonDatabase jsonDatabase) {
        try {
            Socket socket = serverSocket.accept();
            (new SocketWorker(serverSocket, socket, jsonDatabase)).start();
        } catch(Exception e) {
            serverSocket.isClosed();
        }
    }
    public static void main(String[] args) {
        JsonDatabase jsonDatabase = new JsonDatabase();
        try (
                ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))
        ) {
            while (!serverSocket.isClosed()) {
                openSocket(serverSocket, jsonDatabase);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

class SocketWorker extends Thread {
    private final Socket socket;
    private final JsonDatabase jsonDatabase;
    private final ServerSocket serverSocket;

    public SocketWorker(ServerSocket serverSocket, Socket socket, JsonDatabase jsonDatabase) {
        this.socket = socket;
        this.jsonDatabase = jsonDatabase;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
             DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {
             String requestFromClient = inputStream.readUTF();
             Gson gson = new Gson();
             JsonObject json = gson.fromJson(requestFromClient, JsonObject.class);
             if (json.getType() != null) {
                 switch (json.getType()) {
                     case "get":
                         Map<String, String> responseForGet;
                         String gettingValue = jsonDatabase.get(json.getKey());
                         if (!gettingValue.equals(CommandUtils.RESPONSE_ERROR)) {
                             responseForGet = responseOk(gettingValue);
                         } else {
                             responseForGet = responseError();
                         }
                         outputStream.writeUTF(getJsonStrFromMap(gson, responseForGet));
                         break;
                     case "set":
                         Map<String, String> responseForSet = responseOk();
                         jsonDatabase.set(json.getKey(), json.getValue());
                         outputStream.writeUTF(getJsonStrFromMap(gson, responseForSet));
                         break;
                     case "delete":
                         String deletingValue = jsonDatabase.get(json.getKey());
                         Map<String, String> responseForDelete;
                         if (!deletingValue.equals(CommandUtils.RESPONSE_ERROR)) {
                             jsonDatabase.delete(json.getKey());
                             responseForDelete = responseOk();
                         } else {
                             responseForDelete = responseError();
                         }
                         outputStream.writeUTF(getJsonStrFromMap(gson, responseForDelete));
                         break;
                     default:
                         Map<String, String> responseExiting = responseOk();
                         outputStream.writeUTF(getJsonStrFromMap(gson, responseExiting));
                         serverSocket.close();
                         break;
                 }
             }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
    private static String getJsonStrFromMap(Gson gson, Map<String, String> json) {
        return gson.toJson(json);
    }
    private static Map<String, String> responseOk(String...value) {
        Map<String, String> json = new HashMap<>(10);
        json.put(CommandUtils.RESPONSE, CommandUtils.RESPONSE_OK);
        if (value.length != 0) {
            json.put(CommandUtils.VALUE, value[0]);
        }
        return json;
    }
    private static Map<String, String> responseError() {
        Map<String, String> json = new HashMap<>(10);
        json.put(CommandUtils.RESPONSE, CommandUtils.RESPONSE_ERROR);
        json.put(CommandUtils.REASON, CommandUtils.RESPONSE_ERROR_REASON);
        return json;
    }
}
