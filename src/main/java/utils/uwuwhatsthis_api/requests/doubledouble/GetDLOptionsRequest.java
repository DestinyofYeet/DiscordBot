package utils.uwuwhatsthis_api.requests.doubledouble;

import org.json.JSONObject;
import utils.Logger;
import utils.api.generics.RequestType;
import utils.api.generics.ResponseWrapper;
import utils.uwuwhatsthis_api.generics.BaseUwuwhatsthisApiRequest;

import java.util.Objects;

public class GetDLOptionsRequest extends BaseUwuwhatsthisApiRequest {

    Logger logger = new Logger("GetDLOptionsRequest");
    private JSONObject urls = new JSONObject();

    private String error;

    private boolean isSuccessful;

    public GetDLOptionsRequest(String links){
        super();

        setType(RequestType.GET);
        setRoute("/doubledouble/options");

        JSONObject obj = new JSONObject();
        obj.put("links", links);

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

        urls = response.getJsonBody();
        isSuccessful = true;
    }

    public JSONObject getUrls() {
        return urls;
    }

    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
