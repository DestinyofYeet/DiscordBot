package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;

public class Intro {

    private final String name, rarity;

    public Intro(JSONObject gameInfo){
        name = gameInfo.getString("intro");
        rarity = gameInfo.getString("introRarity");
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }
}
