package utils.uwuwhatsthis_api.requests.doubledouble;

import org.json.JSONObject;
import utils.Logger;
import utils.api.generics.RequestType;
import utils.api.generics.ResponseWrapper;
import utils.uwuwhatsthis_api.generics.BaseUwuwhatsthisApiRequest;
import utils.uwuwhatsthis_api.requests.doubledouble.results.SongSearchResult;

import java.util.ArrayList;

public class SearchMusicRequest extends BaseUwuwhatsthisApiRequest {

    private final Logger logger = new Logger("SearchMusicRequest");

    private JSONObject results;
    private String error;
    private boolean isSuccessful;

    public SearchMusicRequest(String query){
        super();

        setType(RequestType.GET);
        setRoute("/doubledouble/search");

        JSONObject object = new JSONObject();

        object.put("query", query);

        setJsonBody(object.toString());
    }
    @Override
    public void onResponse(ResponseWrapper response){
        if (!response.isSuccessful() || !response.getJsonBody().has("results")){
            logger.error("Response is not successful! Code: " + response.getCode() + " | Message: " + response.getReason());
            results = null;
            if (response.getJsonBody().has("error")){
                error = response.getJsonBody().getString("error");
            } else {
                error = response.getBody();
            }
            isSuccessful = false;
        } else {
            results = response.getJsonBody();
            isSuccessful = true;
        }
    }

    public ArrayList<SongSearchResult> getResults() {
        return new ArrayList<>() {{
            for (Object entry : results.getJSONArray("results")) {
                JSONObject real_entry = (JSONObject) entry;

                add(new SongSearchResult(
                        real_entry.getString("type"),
                        real_entry.getString("link"),
                        real_entry.getString("links"),
                        real_entry.optString("album", null),
                        real_entry.getString("artist"),
                        real_entry.getString("name"),
                        real_entry.getString("cover_url")));
            }
        }};

    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public String getError() {
        return error;
    }
}
