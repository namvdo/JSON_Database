package server;

public class ResponseObject {
    private final String response;
    private final String reason;


    public ResponseObject(String response, String reason) {
        this.response = response;
        this.reason = reason;
    }

    public String getResponse() {
        return response;
    }

    public String getReason() {
        return reason;
    }
}
