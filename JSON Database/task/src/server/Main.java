package server;

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author namvdo
 */
public class Main {
    private static final int PORT = 8080;
    private static final String ADDRESS = "localhost";

    private static void openSocket(ServerSocket serverSocket, JsonDatabase jsonDatabase) throws IOException {
        try {
            if (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                (new SocketWorker(serverSocket, socket, jsonDatabase)).start();
            }
        } catch (Exception ignored) {
        }
    }

    public static void main(String[] args) {
        JsonDatabase jsonDatabase = new JsonDatabase();
        try {
            if (JsonDatabase.readFromFile(CommandUtils.SERVER_FILE_LOCATION) != null) {
                jsonDatabase = JsonDatabase.readFromFile(CommandUtils.SERVER_FILE_LOCATION);
            }
        } catch (Exception ignored) {
        }
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(ADDRESS))
        ) {
            while (!serverSocket.isClosed()) {
                openSocket(serverSocket, jsonDatabase);
            }
        } catch (IOException ignored) {
        }
    }


}

class SocketWorker extends Thread {
    private final Socket socket;
    private JsonDatabase jsonDatabase;
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
            ReadWriteLock lock = new ReentrantReadWriteLock();
            Lock readLock = lock.readLock();
            Lock writeLock = lock.writeLock();
            switch (json.getType()) {
                case "get":
                    readLock.lock();
                    try {
                        handleGetJsonRequest(outputStream, gson, json);
                    } finally {
                        readLock.unlock();
                    }
                    break;
                case "set":
                    writeLock.lock();
                    try {
                        Map<String, String> responseForSet = responseOk();
                        jsonDatabase.set(json.getKey(), json.getValue());
                        JsonDatabase.writeToFile(CommandUtils.SERVER_FILE_LOCATION, jsonDatabase);
                        outputStream.writeUTF(getJsonStrFromMap(gson, responseForSet));
                    } finally {
                        writeLock.unlock();
                    }
                    break;
                case "delete":
                    writeLock.lock();
                    try {
                        String deletingValue = jsonDatabase.get(json.getKey());
                        Map<String, String> responseForDelete;
                        if (!deletingValue.equals(CommandUtils.RESPONSE_ERROR)) {
                            jsonDatabase.delete(json.getKey());
                            JsonDatabase.writeToFile(CommandUtils.SERVER_FILE_LOCATION, jsonDatabase);
                            responseForDelete = responseOk();
                        } else {
                            responseForDelete = responseError();
                        }
                        outputStream.writeUTF(getJsonStrFromMap(gson, responseForDelete));
                    } finally {
                        writeLock.unlock();
                    }
                    break;
                default:
                    Map<String, String> responseExiting = responseOk();
                    outputStream.writeUTF(getJsonStrFromMap(gson, responseExiting));
                    socket.close();
                    serverSocket.close();
                    break;
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void handleGetJsonRequest(DataOutputStream outputStream, Gson gson, JsonObject json) throws IOException {
        try {
            Map<String, String> responseForGet;
            jsonDatabase = JsonDatabase.readFromFile(CommandUtils.SERVER_FILE_LOCATION);
            assert jsonDatabase != null;
            String gettingValue = jsonDatabase.get(json.getKey());
            if (!gettingValue.equals(CommandUtils.RESPONSE_ERROR)) {
                responseForGet = responseOk(gettingValue);
            } else {
                responseForGet = responseError();
            }
            outputStream.writeUTF(getJsonStrFromMap(gson, responseForGet));
        } catch (Exception e) {
            outputStream.writeUTF(getJsonStrFromMap(gson, responseError()));
        }
    }

    private static String getJsonStrFromMap(Gson gson, Map<String, String> json) {
        return gson.toJson(json);
    }

    private static Map<String, String> responseOk(String... value) {
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
