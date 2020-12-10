package client;

import com.beust.jcommander.JCommander;
import com.google.gson.JsonObject;
import server.CommandUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.System.out;

/**
 * The type Client.
 */
public class Client {
    /**
     * Send a json request from the client to the server
     *
     * @param args - the provided arguments from the command line
     */
    public void executeClient(String[] args) {
        Args arg = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(arg)
                .build();
        commander.parse(args);
        String address = "127.0.0.1";
        int port = 8080;
        try (
                Socket socket = new Socket(InetAddress.getByName(address), port);
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())) {

            out.println("Client started!");
            JsonObject jsonCommand = new JsonObject();
            // in case user enters a json file.
            if (arg.jsonFile != null) {
                if (JsonCommand.readFromFile(CommandUtils.CLIENT_DATA_FOLDER_LOCATION + arg.jsonFile) != null) {
                    jsonCommand = JsonCommand.readFromFile(CommandUtils.CLIENT_DATA_FOLDER_LOCATION + arg.jsonFile);
                }
            } else {
                jsonCommand.addProperty("type", arg.type);
                if (arg.index != null) {
                    jsonCommand.addProperty("key", arg.index);
                }
                if (arg.message != null) {
                    jsonCommand.addProperty("value", arg.message);
                }
            }
            out.println(CommandUtils.SEND + ": " + jsonCommand.toString());
            outputStream.writeUTF(jsonCommand.getAsJsonObject().toString());
            out.println(CommandUtils.RECEIVE + ": " + inputStream.readUTF());
        } catch (IOException ignored) {
        }
    }
}
