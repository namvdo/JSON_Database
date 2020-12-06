package server;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author namvdo
 */
public class JsonDatabase implements Serializable{
    private final Map<String, String> jsonDb = new HashMap<>(1000);

    public void delete(String key) {
        jsonDb.remove(key);
    }

    public String get(String key) {
        if (jsonDb.get(key) == null) {
            return CommandUtils.RESPONSE_ERROR;
        } else {
            return jsonDb.get(key);
        }
    }

    public void set(String key, String value) {
        jsonDb.put(key, value);
    }

    public static JsonDatabase readFromFile(String fileName) throws IOException {
        try (
             FileInputStream fileInputStream = new FileInputStream(fileName);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream)) {
             return (JsonDatabase) objectInputStream.readObject();
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }

    public static synchronized void writeToFile(String fileName, JsonDatabase json) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName);
             BufferedOutputStream bufferedInputStream = new BufferedOutputStream(fileOutputStream);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedInputStream)) {
            objectOutputStream.writeObject(json);
        }
    }

}
