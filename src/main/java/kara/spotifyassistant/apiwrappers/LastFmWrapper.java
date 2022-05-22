package kara.spotifyassistant.apiwrappers;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LastFmWrapper {

    @Value("${last.fm.api.key}")
    private String lastFmApiKey;

    public JSONObject getSimilarTracks(String song) throws Exception {
        return new JSONObject();
    }

}
