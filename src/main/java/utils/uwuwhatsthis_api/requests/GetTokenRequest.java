package utils.uwuwhatsthis_api.requests;

import main.Main;
import org.json.JSONObject;
import utils.Constants;
import utils.Logger;
import utils.api.BaseRequest;
import utils.api.generics.ResponseWrapper;

import java.util.HashMap;

public class GetTokenRequest extends BaseRequest {

    private static final Logger logger = new Logger("GetTokenRequest");

    public GetTokenRequest(String username, String password){
        super();

        setFullUrl(Constants.UWUWHATSTHIS_API_BASE + "/token/generate");

        JSONObject json = new JSONObject();

        json.put("username", username);
        json.put("password", password);

        setJsonBody(json.toString());

        HashMap<String, String> someHeaders = new HashMap<>(){{
            put("test", "test");
        }};

        setHeaderMap(someHeaders);

    }

    @Override
    public void onResponse(ResponseWrapper response) {
        if (response.isSuccessful()){
            JSONObject json = response.getJsonBody();

            Main.getUwuwhatsthisApiManager().setToken(json.getString("token"));
        } else {
            logger.error("Authentication Request is not successful! Code: " + response.getCode() + " | Message: " + response.getReason() + "| Body: " + response.getBody());
        }
    }
}
