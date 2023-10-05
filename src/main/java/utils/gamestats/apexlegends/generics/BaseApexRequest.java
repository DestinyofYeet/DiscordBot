package utils.gamestats.apexlegends.generics;

import main.Main;
import utils.Constants;
import utils.api.BaseRequest;

import java.util.HashMap;

public class BaseApexRequest extends BaseRequest {

    public BaseApexRequest(){
        super();

        setHeaderMap(new HashMap<>(){{
            put("Authorization", Main.getConfig().getApexLegendsKey());
        }});
    }

    public BaseApexRequest(String route){
        this();
        setRoute(route);
    }

    public void setRoute(String route){
        setFullUrl(Constants.APEX_LEGENDS_API_BASE + route);
    }

}
