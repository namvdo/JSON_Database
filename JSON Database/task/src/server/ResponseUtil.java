package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Response util.
 *
 * @author namvdo
 */
public class ResponseUtil {
    private static final Gson gson = new Gson();

    private ResponseUtil() {

    }

    /**
     * Response ok with value json object.
     *
     * @param value the value
     * @return the json object
     */
    public static JsonObject responseOkWithValue(String value) {
        Map<String, String> json = new HashMap<>(10);
        json.put(CommandUtils.RESPONSE, CommandUtils.RESPONSE_OK);
        json.put(CommandUtils.VALUE, value);
        return JsonParser.parseString(gson.toJson(json)).getAsJsonObject();
    }

    /**
     * Response ok with json obj json object.
     *
     * @param jsonObj the json obj
     * @return the json object
     */
    public static JsonObject responseOkWithJsonObj(JsonObject jsonObj) {
        Map<String, JsonElement> json = new HashMap<>(10);
        json.put(CommandUtils.RESPONSE, JsonParser.parseString(CommandUtils.RESPONSE_OK).getAsJsonPrimitive());
        json.put(CommandUtils.VALUE, jsonObj);
        return JsonParser.parseString(gson.toJson(json)).getAsJsonObject();
    }

    /**
     * Response ok json object.
     *
     * @return the json object
     */
    public static JsonObject responseOk() {
        JsonObject json = new JsonObject();
        json.addProperty(CommandUtils.RESPONSE, CommandUtils.RESPONSE_OK);
        return json;
    }

    /**
     * Response error json object.
     *
     * @return the json object
     */
    public static JsonObject responseError() {
        Map<String, String> json = new HashMap<>(10);
        json.put(CommandUtils.RESPONSE, CommandUtils.RESPONSE_ERROR);
        json.put(CommandUtils.REASON, CommandUtils.RESPONSE_ERROR_REASON);
        return JsonParser.parseString(new Gson().toJson(json)).getAsJsonObject();
    }

    /**
     * Utility function to response ok with either a json object or a string value
     *
     * @param value - either a Json object, or a Json primitive
     * @return JsonObject
     */
    public static JsonObject responseOkBinary(JsonElement value) {
        if (value.isJsonPrimitive()) {
            return responseOkWithValue(value.getAsString());
        } else {
            return responseOkWithJsonObj(value.getAsJsonObject());
        }
    }
}
