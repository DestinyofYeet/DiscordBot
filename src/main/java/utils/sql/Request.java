package utils.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private RequestType type;

    private String sql;
    private ArrayList<String> data;

    private Map<String, String> result;

    private Object lock;

    private Request overwriteRequest;

    private Request(){
        this.lock = new Object();

        this.result = new HashMap<>();

        this.overwriteRequest = null;
    }

    public Request(RequestType type, String sql, ArrayList<String> data){
        this();

        this.type = type;
        this.sql = sql;
        this.data = data == null ? new ArrayList<>() : data;
    }

    public Request(RequestType type, String sql, ArrayList<String> data, Request overwriteRequest){
        this(type, sql, data);

        this.overwriteRequest = overwriteRequest;
    }

    public RequestType getType() {
        return type;
    }

    public String getSql() {
        return sql;
    }

    public ArrayList<String> getData() {
        return data;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }

    public Object getLock() {
        return lock;
    }

    public Request getOverwriteRequest() {
        return overwriteRequest;
    }
}
