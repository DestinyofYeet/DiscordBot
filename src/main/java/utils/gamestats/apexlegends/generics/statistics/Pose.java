package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;

public class Pose {

    private final String name, rarity;

    public Pose(JSONObject selected){
        name = selected.getString("pose");
        rarity = selected.getString("poseRarity");
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }
}
