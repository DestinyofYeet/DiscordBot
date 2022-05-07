package utils.gamestats.apexlegends.generics;

import main.Main;
import utils.Constants;
import utils.api.Request;

import java.util.HashMap;

public class BaseApexRequest extends Request {

    public BaseApexRequest(){
        super();
        this.headerMap = new HashMap<>(){{
            put("Authorization", Main.getConfig().getApexLegendsKey());
        }};
    }

    public BaseApexRequest(String route){
        this();
        this.fullUrl = Constants.APEX_LEGENDS_API_BASE + route;
    }

    public void setRoute(String route){
        this.fullUrl = Constants.APEX_LEGENDS_API_BASE + route;
    }

}
