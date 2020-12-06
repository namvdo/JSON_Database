package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import server.CommandUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

/**
 * @author namvdo
 */
public class Main{

    public static void main(String[] args) {
        Args arg = new Args();
        JCommander commander = JCommander.newBuilder()
                .addObject(arg)
                .build();
        commander.parse(args);

        String address = "127.0.0.1";
        int port = 8080;
        try
         {
            Socket socket = new Socket(InetAddress.getByName(address), port);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            out.println("Client started!");
            String commandType = "";
            String key = "";
            String value = "";
            if (arg.jsonFile != null) {
                if (JsonObject.readFromFile(CommandUtils.CLIENT_DATA_FOLDER_LOCATION + arg.jsonFile) != null) {
                    JsonObject jsonObject = JsonObject.readFromFile(CommandUtils.CLIENT_DATA_FOLDER_LOCATION + arg.jsonFile);
                    commandType = jsonObject.getType();
                    key = jsonObject.getKey();
                    value = jsonObject.getValue();
                }
            } else {
                commandType = arg.type;
                key = arg.index;
                value = arg.message;
            }
            switch (commandType) {
                case "get":
                    Map<String, String> getJson = new HashMap<>(10);
                    getJson.put("type", "get");
                    getJson.put("key", key + "");
                    Gson gsonGet = new Gson();
                    String jsonGetOutput = gsonGet.toJson(getJson);
                    out.println(CommandUtils.SEND + ": " + jsonGetOutput);
                    outputStream.writeUTF(jsonGetOutput);
                    out.println(CommandUtils.RECEIVE + ": " + inputStream.readUTF());
                    break;
                case "set":
                    Map<String, String> setJson = new HashMap<>(10);
                    setJson.put("type", "set");
                    setJson.put("key", key + "");
                    setJson.put("value", value);
                    Gson gsonSet = new Gson();
                    String jsonSetOutput = gsonSet.toJson(setJson);
                    out.println(CommandUtils.SEND + ": " + jsonSetOutput);
                    outputStream.writeUTF(jsonSetOutput);
                    out.println(CommandUtils.RECEIVE + ": " + inputStream.readUTF());
                    break;
                case "delete":
                    Map<String, String> jsonDelete = new HashMap<>(10);
                    jsonDelete.put("type", "delete");
                    jsonDelete.put("key", key + "");
                    Gson gsonDelete = new Gson();
                    String jsonDeleteOutput = gsonDelete.toJson(jsonDelete);
                    out.println(CommandUtils.SEND + ": " + jsonDeleteOutput);
                    outputStream.writeUTF(jsonDeleteOutput);
                    out.println(CommandUtils.RECEIVE + ": " + inputStream.readUTF());
                    break;
                default:
                    Map<String, String> jsonExit = new HashMap<>(10);
                    jsonExit.put("type", "exit");
                    Gson gsonExit = new Gson();
                    String jsonExitOutput = gsonExit.toJson(jsonExit);
                    out.println(CommandUtils.SEND + ": " + jsonExitOutput);
                    outputStream.writeUTF(jsonExitOutput);
                    out.println(CommandUtils.RECEIVE + ": " + inputStream.readUTF());
            }

        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }
}


