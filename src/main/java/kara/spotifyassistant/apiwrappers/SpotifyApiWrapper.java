package kara.spotifyassistant.apiwrappers;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.config.Util;
import kara.spotifyassistant.security.SecurityUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
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

    public enum TIME_RANGE {
        medium_term, short_term, long_term
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
        System.out.println(responseBody.getString("access_token"));
        return responseBody.getString("access_token");
    }

    public JSONObject getTopItems(String clientId, GetTopItemsRequestParams requestParams) throws Exception {
        String url = new Util.UrlBuilder("https://api.spotify.com/v1/me/top/" + requestParams.itemType)
                .withParams("time_range", Optional.ofNullable(requestParams.timeRange).orElse(TIME_RANGE.medium_term).toString())
                .withParams("limit", Optional.ofNullable(requestParams.limit).orElse(12).toString())
                .withParams("offset", Optional.ofNullable(requestParams.offset).orElse(0).toString())
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + fetchAccessToken(clientId))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public JSONObject getCurrentTrack(String id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/me/player/currently-playing"))
                .header("Authorization", "Bearer " + fetchAccessToken(id))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public JSONObject createPlaylist(String id, CreatePlaylistRequestBody body) throws Exception {
        String spotifyId = appUserSevice.findUserById(id).getSpotifyId();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/users/" + spotifyId  + "/playlists"))
                .header("Authorization", "Bearer " + fetchAccessToken(id))
                .POST(HttpRequest.BodyPublishers.ofString(
                        new JSONObject().put("name", body.name)
                                .put("description", body.description)
                                .put("public", body._public)
                                .toString()
                ))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        return new JSONObject(response.body());
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter @Setter
    public static class GetTopItemsRequestParams {
        @NotNull
        private ITEM_TYPE itemType;

        private TIME_RANGE timeRange;

        @Min(0) @Max(15)
        private Integer limit;

        @Min(0) @Max(10)
        private Integer offset;
    }

    @ToString
    @Getter @Setter
    public static class CreatePlaylistRequestBody {
        @NotNull
        private String name;

        @NotNull
        private String description;

        @NotNull
        private boolean _public;
    }

}
