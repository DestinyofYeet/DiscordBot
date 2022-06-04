package utils.gamestats.apexlegends.generics.statistics;

import org.json.JSONObject;
import utils.Constants;

public class Rank {

    private final String rankName, rankImg, rankedSeason, rankedSeasonRaw;

    private final int rankScore, rankDiv;

    public Rank(JSONObject rank){
        rankName = rank.getString("rankName");
        rankScore = rank.getInt("rankScore");;
        rankDiv = rank.getInt("rankDiv");
        rankImg = rank.getString("rankImg");

        rankedSeasonRaw = rank.getString("rankedSeason");
        rankedSeason = Constants.capitalizeString(rankedSeasonRaw.replace("_", " "));
    }

    public String getRankName() {
        return rankName;
    }

    public String getRankImg() {
        return rankImg;
    }

    public String getRankedSeason() {
        return rankedSeason;
    }

    public String getRankedSeasonRaw() {
        return rankedSeasonRaw;
    }

    public int getRankScore() {
        return rankScore;
    }

    public int getRankDiv() {
        return rankDiv;
    }
}
