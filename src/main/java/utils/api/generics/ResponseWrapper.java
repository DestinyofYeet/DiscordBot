package utils.api.generics;

import org.json.JSONObject;

public class ResponseWrapper {

    private String reason, body;
    private int code;

    private JSONObject jsonBody;

    public ResponseWrapper(){

    }

    public ResponseWrapper(int code, String reason, String body){
        this.code = code;
        this.reason = reason;
        this.body = body;
    }

    public String getReason() {
        return reason;
    }

    public String getBody() {
        return body;
    }

    public int getCode() {
        return code;
    }

    public JSONObject getJsonBody() {
        return new JSONObject(body);
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccessful(){
        return this.code == 200;
    }
}
