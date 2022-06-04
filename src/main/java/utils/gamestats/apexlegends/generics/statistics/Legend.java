package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class Legend {

    private List<Badge> badges;
    private List<Data> data;

    private Frame frame;
    private Pose pose;
    private Intro into;

    private String name, iconURL, bannerURL;

    public Legend(JSONObject selected){
        JSONArray shownStats = selected.getJSONArray("data");

        data = new LinkedList<>();

        for (Object shownStat : shownStats) {
            JSONObject stat = (JSONObject) shownStat;
            data.add(new Data(stat));
        }

        JSONObject gameInfo = selected.getJSONObject("gameInfo");

        JSONArray badgesShown = gameInfo.getJSONArray("badges");

        badges = new LinkedList<>();

        for (Object shownBadge: badgesShown){
            JSONObject badge = (JSONObject) shownBadge;
            badges.add(new Badge(badge));
        }

        JSONObject imgAssets = selected.getJSONObject("ImgAssets");

        iconURL = imgAssets.getString("icon");
        bannerURL = imgAssets.getString("banner");

        name = selected.getString("LegendName");
    }

    public List<Badge> getBadges() {
        return badges;
    }

    public List<Data> getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public String getIconURL() {
        return iconURL;
    }

    public String getBannerURL() {
        return bannerURL;
    }
}
