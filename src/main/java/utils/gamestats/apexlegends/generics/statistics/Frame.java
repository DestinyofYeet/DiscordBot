package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;

public class Frame {

    private final String name, rarity;

    public Frame(JSONObject selected){
        name = selected.getString("frame");
        rarity = selected.getString("frameRarity");
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }
}
