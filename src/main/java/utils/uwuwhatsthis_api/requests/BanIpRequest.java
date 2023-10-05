package utils.uwuwhatsthis_api.requests;

import okhttp3.Response;
import org.json.JSONObject;
import utils.Logger;
import utils.api.generics.RequestType;
import utils.api.generics.ResponseWrapper;
import utils.uwuwhatsthis_api.generics.BaseUwuwhatsthisApiRequest;

import java.io.IOException;

public class BanIpRequest extends BaseUwuwhatsthisApiRequest {

    private final Logger logger = new Logger("BanIpRequest");

    private String message;
    private boolean isSuccessful;

    public BanIpRequest(String ip, String reason){
        super();

        setType(RequestType.POST);
        setRoute("/ip/blacklist/add");

        JSONObject object = new JSONObject();

        object.put("ip", ip);
        object.put("reason", reason);

        setJsonBody(object.toString());
    }

    @Override
    public void onResponse(ResponseWrapper response){
        if (!response.isSuccessful()){
            logger.error("Response is not successful! Code: " + response.getCode() + " | Message: " + response.getReason());
            message = null;
            isSuccessful = false;
        } else {

            JSONObject json;

            json = response.getJsonBody();

            message = json.getString("message");
            isSuccessful = true;
        }
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}
