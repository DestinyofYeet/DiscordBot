package utils.gamestats.apexlegends;

import okhttp3.Response;
import org.json.JSONObject;
import utils.Logger;
import utils.api.generics.ResponseWrapper;
import utils.gamestats.apexlegends.generics.BaseApexRequest;
import utils.gamestats.apexlegends.generics.statistics.ApexPlayer;

import java.io.IOException;

public class GetPlayerStatsByNameRequest extends BaseApexRequest {
    private ApexPlayer apexPlayer;
    
    private String playerName;

    private ResponseWrapper response;

    private final static Logger logger = new Logger("GetPlayerStatsByNameRequest");


    public GetPlayerStatsByNameRequest(String playerName, String platform) {
        super();
        apexPlayer = null;
        this.playerName = playerName;
        String route = "/bridge?player=" + playerName + "&platform=" + platform;

        this.setRoute(route);
    }

    @Override
    public void onResponse(ResponseWrapper response){
        if (!response.isSuccessful()){
            logger.error("Request is not successful! Code: " + response.getCode());
            this.response = response;
            return;
        }

        JSONObject data;

        data = response.getJsonBody();

        if (data.has("Error")){
           this.apexPlayer = null;
           return;
       }

        this.apexPlayer = new ApexPlayer(data);
    }

    public ApexPlayer getApexPlayer() {
        return apexPlayer;
    }

    public ResponseWrapper getResponse() {
        return response;
    }
}
