package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;

public class Skin {

    private final String rarity, name;

    public Skin(JSONObject gameInfo){
        rarity = gameInfo.getString("skinRarity");
        name = gameInfo.getString("skin");
    }

    public String getRarity() {
        return rarity;
    }

    public String getName() {
        return name;
    }
}
