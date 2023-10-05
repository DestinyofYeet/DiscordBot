package utils.uwuwhatsthis_api;

import main.Main;
import utils.Logger;
import utils.api.BaseRequest;
import utils.uwuwhatsthis_api.requests.GetTokenRequest;

import java.io.IOException;

public class UwuwhatsthisApiManager {

    private final String username, password;

    private final Logger logger = new Logger("UwuwhatsthisApiManager");

    private String token;

    public UwuwhatsthisApiManager(){
        this.username = Main.getConfig().getYeetApiUsername();
        this.password = Main.getConfig().getYeetApiPassword();
    }

    public void authorize(){
        logger.info("Started authorization request!");
        BaseRequest request = new GetTokenRequest(this.username, this.password);

        try {
            request.doRequest();
            if (token != null) {
                logger.info("Authorization successful!");
            } else {
                logger.error("Authorization failed!");
            }

        } catch (IOException e) {
            logger.error("Authorization failed! " + e);
            throw new RuntimeException(e);
        }
    }

    public void setToken(String token){
        this.token = token;
    }

    public String getToken(){
        if (token == null){
            authorize();
        }

        return token;
    }
}
