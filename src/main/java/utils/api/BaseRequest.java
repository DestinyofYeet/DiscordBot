package utils.api;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import utils.api.generics.HttpGetWithEntity;
import utils.api.generics.RequestType;
import utils.api.generics.ResponseWrapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseRequest {

    private String fullUrl;
    private HashMap<String, String> headerMap;
    private RequestType type;

    private String jsonBody;

    public BaseRequest(){
        this.type = RequestType.GET;
    }

    public void doRequest() throws IOException{
        if (fullUrl == null){
            throw new IllegalArgumentException("Url may not be null");
        }

        try(CloseableHttpClient httpClient = HttpClients.createDefault()){

            HttpEntityEnclosingRequestBase get;

            switch (type){
                case GET -> {
                    get = new HttpGetWithEntity();
                }

                case POST -> {
                    get = new HttpPost();
                }

                default -> {
                    throw new IllegalArgumentException("Invalid / Unsupported request type!");
                }
            }

            List<Header> headers = new ArrayList<>(){{

                if (headerMap != null){
                    headerMap.forEach((key, value) -> {
                        add(new BasicHeader(key, value));
                    });
                }
            }};



            if (headerMap != null){
                Header[] headerArray = new Header[headers.size()];

                for (int i = 0; i < headerArray.length; i++){
                    headerArray[i] = headers.get(i);
                }

                get.setHeaders(headerArray);
            }


            if (jsonBody != null){
                StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
                get.setEntity(entity);
            }



            get.setURI(new URI(fullUrl));
            CloseableHttpResponse response = httpClient.execute(get);

            ResponseWrapper wrapper = new ResponseWrapper();

            wrapper.setCode(response.getStatusLine().getStatusCode());
            wrapper.setReason(response.getStatusLine().getReasonPhrase());

            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null){
                String result = EntityUtils.toString(responseEntity);

                wrapper.setBody(result);
            }

            onResponse(wrapper);


        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Url is not a valid URI: " + fullUrl + " | " + e);
        }

    }

    public void onResponse(ResponseWrapper wrapper){};

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(HashMap<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public String getJsonBody() {
        return jsonBody;
    }

    public void setJsonBody(String jsonBody) {
        this.jsonBody = jsonBody;
    }
}
