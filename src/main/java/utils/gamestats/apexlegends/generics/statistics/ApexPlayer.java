package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;
import utils.Constants;

public class ApexPlayer {
    private final boolean partyFull;
    private final boolean online;
    private final boolean canJoin;

    private final String lobbyState;
    private final String currentStateAsText;
    private final String selectedLegendString;

    private final Legend selectedLegend;

    private final int uid, toNextLevelPercent, level;

    private final String name, platform;

    private final Rank brRanked, arenaRanked;

    public ApexPlayer(JSONObject data){
        JSONObject realtime = data.getJSONObject("realtime");

        partyFull = Constants.intToBool(realtime.getInt("partyFull"));
        online = Constants.intToBool(realtime.getInt("isOnline"));
        canJoin = Constants.intToBool(realtime.getInt("canJoin"));

        lobbyState = realtime.getString("lobbyState");
        currentStateAsText = realtime.getString("currentStateAsText");
        selectedLegendString = realtime.getString("selectedLegend");

        JSONObject legends = data.getJSONObject("legends");

        JSONObject selected = legends.getJSONObject("selected");

       selectedLegend = new Legend(selected);

       JSONObject global = data.getJSONObject("global");

       uid = global.getInt("uid");
       toNextLevelPercent = global.getInt("toNextLevelPercent");
       level = global.getInt("level");

       name = global.getString("name");
       platform = global.getString("platform");

       brRanked = new Rank(global.getJSONObject("rank"));

       arenaRanked = new Rank(global.getJSONObject("arena"));
    }

    public boolean isPartyFull() {
        return partyFull;
    }

    public boolean isOnline() {
        return online;
    }

    public boolean isCanJoin() {
        return canJoin;
    }

    public String getLobbyState() {
        return lobbyState;
    }

    public String getCurrentStateAsText() {
        return currentStateAsText;
    }

    public String getSelectedLegendString() {
        return selectedLegendString;
    }

    public Legend getSelectedLegend() {
        return selectedLegend;
    }

    public int getUid() {
        return uid;
    }

    public int getToNextLevelPercent() {
        return toNextLevelPercent;
    }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public String getPlatform() {
        return platform;
    }

    public Rank getBrRanked() {
        return brRanked;
    }

    public Rank getArenaRanked() {
        return arenaRanked;
    }
}
