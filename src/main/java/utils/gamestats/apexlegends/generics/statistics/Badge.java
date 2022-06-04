package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;

public class Badge {

    private final String name, category;

    private final int value;

    public Badge(JSONObject badgeInfo){
        name = badgeInfo.getString("name");
        category = badgeInfo.getString("category");
        value = badgeInfo.getInt("value");
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getValue() {
        return value;
    }
}
