package client;

public class JsonObject {
    private final String type;
    private final String key;
    private final String value;
    private JsonObject(JsonObjectBuilder builder) {
        this.type = builder.type;
        this.key = builder.key;
        this.value = builder.value;
    }
    public static class JsonObjectBuilder {
        private final String type;
        private String key;
        private String value;
        public JsonObjectBuilder(String type) {
            this.type = type;
        }
        public JsonObjectBuilder key(String key) {
            this.key = key;
            return this;
        }
        public JsonObjectBuilder value(String value) {
            this.value = value;
            return this;
        }
        public JsonObject build() {
            return new JsonObject(this);
        }
    }
    public static JsonObjectBuilder Builder(String type) {
        return new JsonObjectBuilder(type);
    }
    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
}

