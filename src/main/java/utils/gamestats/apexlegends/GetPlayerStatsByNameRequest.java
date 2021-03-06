package utils.gamestats.apexlegends;

import okhttp3.Response;
import org.json.JSONObject;
import utils.Logger;
import utils.gamestats.apexlegends.generics.BaseApexRequest;
import utils.gamestats.apexlegends.generics.statistics.ApexPlayer;

import java.io.IOException;

public class GetPlayerStatsByNameRequest extends BaseApexRequest {
    private ApexPlayer apexPlayer;
    
    private String playerName;

    private Response response;

    private final static Logger logger = new Logger("GetPlayerStatsByNameRequest");


    public GetPlayerStatsByNameRequest(String playerName, String platform) {
        super();
        apexPlayer = null;
        this.playerName = playerName;
        String route = "/bridge?player=" + playerName + "&platform=" + platform;

        this.setRoute(route);
    }

    @Override
    public void onSuccess(Response response){
        if (!response.isSuccessful()){
            logger.error("Request is not successful! Code: " + response.code());
            this.response = response;
            return;
        }

        JSONObject data;

        try {
            data = new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

       if (data.has("Error")){
           this.apexPlayer = null;
           return;
       }

        this.apexPlayer = new ApexPlayer(data);
    }

    public ApexPlayer getApexPlayer() {
        return apexPlayer;
    }

    public Response getResponse() {
        return response;
    }
}
