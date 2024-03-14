package utils.uwuwhatsthis_api.requests.doubledouble;

import org.json.JSONObject;
import utils.Logger;
import utils.api.generics.RequestType;
import utils.api.generics.ResponseWrapper;
import utils.uwuwhatsthis_api.generics.BaseUwuwhatsthisApiRequest;

import java.util.Objects;

public class GetDownloadStatusRequest extends BaseUwuwhatsthisApiRequest {

    Logger logger = new Logger("GetDLOptionsRequest");
    private JSONObject response = new JSONObject();

    private String error;

    private boolean isSuccessful;
    public GetDownloadStatusRequest(String downloadId){
        super();
        setRoute("/doubledouble/status");
        setType(RequestType.GET);

        JSONObject obj = new JSONObject();
        obj.put("download_id", downloadId);

        setJsonBody(obj.toString());
    }

    @Override
    public void onResponse(ResponseWrapper response){
        if (!response.isSuccessful()){
            logger.error("Response is not successful! Code: " + response.getCode() + " | Message: " + response.getReason());
            error = response.getBody();
            isSuccessful = false;
            return;
        }

        if (response.getJsonBody().has("error")){
            error = response.getJsonBody().getString("error");
            logger.error("Response failed: " + error);
            isSuccessful = false;
            return;
        }

        this.response = response.getJsonBody();
        isSuccessful = true;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getStatus(){
        logger.info(this.response.toString());
        return this.response.getString("status");
    }
}
