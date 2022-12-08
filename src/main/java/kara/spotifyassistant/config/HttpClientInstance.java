package kara.spotifyassistant.config;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class HttpClientInstance {

    private final HttpClient httpClient = HttpClient.newHttpClient();

//    public JSONObject get(String url) throws Exception {
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(url))
//                .build();
//        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//        JSONObject responseBody = new JSONObject(response.body());
//    }

}
