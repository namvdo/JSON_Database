package server;

import client.CommandUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author namvdo
 */
public class JsonDatabase {
    private final Map<String, String> jsonDb = new HashMap<>(1000);

    public String delete(String key) {

        jsonDb.remove(key);
        return CommandUtils.RESPONSE_OK;
    }

    public String get(String key) {
        if (jsonDb.get(key) == null) {
            return CommandUtils.RESPONSE_ERROR;
        } else {
            return jsonDb.get(key);
        }
    }

    public String set(String key, String value) {
        jsonDb.put(key, value);
        return CommandUtils.RESPONSE_OK;
    }

}
