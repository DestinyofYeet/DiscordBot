package utils.api;

import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.IOException;
import java.util.HashMap;

public abstract class Request {

    protected String fullUrl;
    protected HashMap<String, String> headerMap;

    public Request(){
        this.fullUrl = null;
        this.headerMap = null;
    }

    public Request(String fullUrl){
        this();
        this.fullUrl = fullUrl;
    }

    public Request(String fullUrl, HashMap<String, String> headerMap){
        this(fullUrl);
        this.headerMap = headerMap;
    }

    public void doRequest() throws IOException {
        if (fullUrl == null){
            throw new IllegalArgumentException("Url may not be null!");
        }

        OkHttpClient client = new OkHttpClient();

        okhttp3.Request request;

        okhttp3.Request.Builder getBuilder = new okhttp3.Request.Builder()
                .url(fullUrl)
                .get();

        if (headerMap != null){
            for (String key: this.headerMap.keySet()){
                getBuilder.addHeader(key, this.headerMap.get(key));
            }
        }

        request = getBuilder.build();

        Response response = client.newCall(request).execute();

        onSuccess(response);

        response.close();
    }

    public void onSuccess(Response response){

    }


}
