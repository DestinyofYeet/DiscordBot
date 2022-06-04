package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;

public class Data {

    private final String name, key;

    private final boolean global;

    private final int value;

    public Data(JSONObject data){
        name = data.getString("name");
        global = data.getBoolean("global");
        value = data.getInt("value");
        key = data.getString("key");
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public boolean isGlobal() {
        return global;
    }

    public int getValue() {
        return value;
    }
}
