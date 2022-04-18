package kara.spotifyassistant.spotifyapiwrapper;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.security.SecurityUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
public class SpotifyApiWrapper {

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    private AppUserService appUserSevice;

    private SecurityUtil securityUtil;

    public enum ITEM_TYPE {
        artists, tracks
    }

    @Autowired
    public SpotifyApiWrapper(AppUserService appUserSevice, SecurityUtil securityUtil) {
        this.appUserSevice = appUserSevice;
        this.securityUtil = securityUtil;
    }

    private HttpClient httpClientInstance = HttpClient.newHttpClient();


    private JSONObject get(String url) {
        return null;
    }

    private String fetchAccessToken(String id) throws Exception {

        AppUser appUser = appUserSevice.findUserById(id);

        var form = new HashMap<String, String>() {{
            put("grant_type", "refresh_token");
            put("refresh_token", appUser.getRefreshToken());
        }};

        String requestBody = form.entrySet()
                .stream()
                .map(data -> data.getKey() + "=" + URLEncoder.encode(data.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://accounts.spotify.com/api/token"))
                .header("Authorization", "Basic " + securityUtil.getEncodedAuthHeader())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.toString())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject responseBody = new JSONObject(response.body());
        return responseBody.getString("access_token");
    }

    public JSONObject getTopItems(String id, ITEM_TYPE itemType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/me/top/" + itemType))
                .header("Authorization", "Bearer " + fetchAccessToken(id))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public JSONObject getCurrentTrack(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/me/player/currently-playing"))
                .header("Authorization", "Bearer " + fetchAccessToken(id))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public void createPlaylist(String id) {}

}
