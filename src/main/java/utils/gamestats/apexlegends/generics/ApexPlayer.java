package utils.gamestats.apexlegends.generics;

import org.json.JSONObject;
import utils.Constants;

public class ApexPlayer {
    private final boolean partyFull;
    private final boolean online;
    private final boolean canJoin;

    private final String lobbyState;
    private final String currentStateAsText;
    private final String selectedLegend;

    public ApexPlayer(JSONObject data){
        JSONObject realtime = data.getJSONObject("realtime");

        partyFull = Constants.intToBool(realtime.getInt("partyFull"));
        online = Constants.intToBool(realtime.getInt("isOnline"));
        canJoin = Constants.intToBool(realtime.getInt("canJoin"));

        lobbyState = realtime.getString("lobbyState");
        currentStateAsText = realtime.getString("currentStateAsText");
        selectedLegend = realtime.getString("selectedLegend");

        JSONObject legends = data.getJSONObject("legends");

        System.out.println(legends.toString(2));
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

    public String getSelectedLegend() {
        return selectedLegend;
    }
}
