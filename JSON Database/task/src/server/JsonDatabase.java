package server;

import com.google.gson.*;

import java.io.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static server.ResponseUtil.*;

/**
 *
 * @author namvdo
 */
public class JsonDatabase {
    private final JsonObject jsonDb;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public JsonDatabase() throws IOException {
        if (readFromFile(CommandUtils.SERVER_FILE_LOCATION) != null) {
            jsonDb = readFromFile(CommandUtils.SERVER_FILE_LOCATION);
        } else {
            jsonDb = new JsonObject();
        }
    }

    /**
     * Read a Json object from a provided file
     *
     * @param serverFileLocation the server file location
     * @return the json object read from file
     * @throws IOException the io exception
     */
    public JsonObject readFromFile(String serverFileLocation) throws IOException {
        readLock.lock();
        try (
                FileReader reader = new FileReader(serverFileLocation);
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                json.append(line);
            }
            return new Gson().fromJson(json.toString(), JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            readLock.unlock();
        }
    }


    /**
     * Get the JSON object based on the key(s) provided, the keys
     * can either be a string, or a json array
     *
     * @param keys the keys
     * @return either a json primitive or a json object
     */
    public JsonElement get(JsonElement keys) {
        if (keys.isJsonPrimitive() || keys.getAsJsonArray().size() == 1) {
            if (jsonDb.has(keys.getAsString())) {
                return responseOkBinary(jsonDb.get(keys.getAsString()));
            } else {
                return responseError();
            }
        } else {
            JsonArray searchingKeys = keys.getAsJsonArray();
            JsonElement elem = jsonDb.get(searchingKeys.get(0).getAsString());
            int keySize = searchingKeys.size();
            for (int i = 1; i < keySize; i++) {
                elem = elem.getAsJsonObject().get(searchingKeys.get(i).getAsString());
                if (elem == null || elem.isJsonPrimitive() && i != keySize - 1) {
                    return responseError();
                } else if (elem.isJsonObject() && i != keySize - 1) {
                    elem = elem.getAsJsonObject();
                } else if (elem.isJsonPrimitive() && i == keySize - 1 || elem.isJsonObject() && i == keySize - 1) {
                    return responseOkBinary(elem);
                } else if (!elem.getAsJsonObject().has(searchingKeys.get(i).getAsString())) {
                    return responseError();
                }
            }
            return null;
        }
    }


    /**
     * Set the Json values with the associated provided key.
     *
     * @param keys - the key from the Json object to add new values
     * @param values - the values need to add to the corresponding key
     * @return the Json object response.
     * @throws IOException the io exception
     */
    public JsonObject set(JsonElement keys, JsonElement values) throws IOException {
        if (keys.isJsonPrimitive() || keys.getAsJsonArray().size() == 1) {
            jsonDb.add(keys.getAsString(), values);
        } else {
            JsonArray keysForSetting = keys.getAsJsonArray();
            int keySize = keysForSetting.size();
            JsonElement currentJson = jsonDb.get(keysForSetting.get(0).getAsString());
            for (int i = 1; i < keySize; i++) {
                JsonObject previous = currentJson.getAsJsonObject();
                if (currentJson.isJsonPrimitive() && i != keySize - 1) {
                    previous.add(currentJson.getAsString(), new JsonObject());
                } else if (currentJson == null && i != keySize - 1) {
                    previous.add(keysForSetting.get(i).getAsString(), new JsonObject());
                } else if (currentJson.isJsonObject() && i != keySize - 1) {
                    currentJson = currentJson.getAsJsonObject();
                }
                if (i == keySize - 1) {
                    previous.add(keysForSetting.get(i).getAsString(), values);
                }
                currentJson = currentJson.getAsJsonObject().get(keysForSetting.get(i).getAsString());
            }

        }

        writeToFile(CommandUtils.SERVER_FILE_LOCATION);
        return responseOk();
    }


    /**
     * Delete the last key (if provided as a Json array)
     * or delete the key from the the Json object
     * @param keys - provided a Json element, the last key will be deleted
     * @return - response Json object indicates fail or success.
     * @throws IOException the io exception
     */
    public JsonObject delete(JsonElement keys) throws IOException {
        if (keys.isJsonPrimitive() || keys.getAsJsonArray().size() == 1) {
            if (!jsonDb.has(keys.getAsString())) {
                return responseError();
            } else {
                jsonDb.remove(keys.getAsString());
                writeToFile(CommandUtils.SERVER_FILE_LOCATION);
                return responseOk();
            }
        } else {
            JsonArray keysToDelete = keys.getAsJsonArray();
            int keySize = keysToDelete.size();
            JsonElement elem = jsonDb.get(keysToDelete.get(0).getAsString());
            for (int i = 1; i < keySize; i++) {
                JsonObject previous = elem.getAsJsonObject();
                String currentKey = keysToDelete.get(i).getAsString();
                if (elem.isJsonPrimitive() && i != keySize - 1 || elem == null) {
                    return responseError();
                } else if (elem.isJsonObject() && i != keySize - 1) {
                    elem = elem.getAsJsonObject();
                }
                if (i == keySize - 1) {
                    if (previous.has(currentKey)) {
                        previous.remove(currentKey);
                        writeToFile(CommandUtils.SERVER_FILE_LOCATION);
                        return responseOk();
                    } else {
                        return responseError();
                    }
                }
                elem = elem.getAsJsonObject().get(keysToDelete.get(i).getAsString());
            }
        }
        return responseError();
    }


    /**
     * Write the current Json object to a file.
     *
     * @param fileName the file name
     * @throws IOException the io exception
     */
    public void writeToFile(String fileName) throws IOException {
        writeLock.lock();
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(new Gson().toJson(jsonDb));
        } finally {
            writeLock.unlock();
        }
    }


}
