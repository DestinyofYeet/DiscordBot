package utils.uwuwhatsthis_api.requests.doubledouble;

import org.json.JSONObject;
import utils.Logger;
import utils.api.generics.RequestType;
import utils.api.generics.ResponseWrapper;
import utils.uwuwhatsthis_api.generics.BaseUwuwhatsthisApiRequest;

import java.util.Objects;

public class DownloadMusicRequest extends BaseUwuwhatsthisApiRequest {

    Logger logger = new Logger("GetDLOptionsRequest");
    private JSONObject response = new JSONObject();

    private String error;

    private boolean isSuccessful;

    public DownloadMusicRequest(String url, String platform){
        super();
        setType(RequestType.GET);
        setRoute("/doubledouble/download");

        JSONObject json = new JSONObject();
        json.put("url", url);
        json.put("platform", platform);

        setJsonBody(json.toString());
    }

    @Override
    public void onResponse(ResponseWrapper response){
        if (!response.isSuccessful()){
            logger.error("Response is not successful! Code: " + response.getCode() + " | Message: " + response.getReason());
            error = response.getBody();
            isSuccessful = false;
            return;
        }

        this.response = response.getJsonBody();
        isSuccessful = true;
    }

    public String getDownloadId(){
        return this.response.getString("download_id");
    }

    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
