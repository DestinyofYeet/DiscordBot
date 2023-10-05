package utils.uwuwhatsthis_api.generics;

import main.Main;
import utils.Constants;
import utils.api.BaseRequest;
import utils.uwuwhatsthis_api.UwuwhatsthisApiManager;

import java.util.HashMap;

public class BaseUwuwhatsthisApiRequest extends BaseRequest {

    private final UwuwhatsthisApiManager manager = Main.getUwuwhatsthisApiManager();

    public BaseUwuwhatsthisApiRequest(){
        super();

        setHeaderMap(new HashMap<>(){{
            put("token", manager.getToken());
        }});
    }

    public void setRoute(String route){
        setFullUrl(Constants.UWUWHATSTHIS_API_BASE + route);
    }
}
