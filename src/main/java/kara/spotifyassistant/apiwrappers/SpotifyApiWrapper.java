package kara.spotifyassistant.apiwrappers;

import kara.spotifyassistant.Models.EncryptedData;
import kara.spotifyassistant.Models.SpotifyToken;
import kara.spotifyassistant.Models.Track;
import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.services.AppUserService;
import kara.spotifyassistant.config.Util;
import kara.spotifyassistant.security.SecurityUtil;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SpotifyApiWrapper {

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    private final SecurityUtil securityUtil;

    public enum ITEM_TYPE {
        artists, tracks
    }

    public enum TIME_RANGE {
        medium_term, short_term, long_term
    }

    @Autowired
    public SpotifyApiWrapper(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }

    public String fetchAccessToken(EncryptedData refreshToken) throws Exception {
        var form = new HashMap<String, String>() {{
            put("grant_type", "refresh_token");
            put("refresh_token", securityUtil.decrypt(refreshToken));
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

    public JSONObject getTopItems(String accessToken, GetTopItemsRequestParams requestParams) throws Exception {
        String url = new Util.UrlBuilder("https://api.spotify.com/v1/me/top/" + requestParams.itemType)
                .withParams("time_range", Optional.ofNullable(requestParams.timeRange).orElse(TIME_RANGE.medium_term).toString())
                .withParams("limit", Optional.ofNullable(requestParams.limit).orElse(12).toString())
                .withParams("offset", Optional.ofNullable(requestParams.offset).orElse(0).toString())
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public JSONObject getUserRecentlyPlayed(String accessToken, FetchRecentlyPlayedParams params) throws Exception {
        String url = new Util.UrlBuilder("https://api.spotify.com/v1/me/player/recently-played/")
            .withParams("after", String.valueOf(params.after))
            .withParams("before", String.valueOf(params.before))
            .withParams("limit", Optional.ofNullable(params.limit).orElse(12).toString())
            .withParams("offset", Optional.ofNullable(params.offset).orElse(0).toString())
            .build();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI("https://api.spotify.com/v1/me/player/recently-played"))
            .header("Authorization", "Bearer " + accessToken)
            .GET()
            .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public JSONObject getCurrentTrack(String accessToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/me/player/currently-playing"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public String createPlaylist(String spotifyId, String accessToken, CreatePlaylistRequestBody body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/users/" + spotifyId  + "/playlists"))
                .header("Authorization", "Bearer " + accessToken)
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
        return new JSONObject(response.body()).getString("id");
    }

    public JSONArray getPlaylistTracks(String accessToken, String playlistId) throws Exception {
        String url = new Util.UrlBuilder("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks")
                .withParams("limit", "50")
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body()).getJSONArray("items");
    }

    public void deleteTracksFromPlaylist(String accessToken, String playlistId, List<String> trackIds) throws Exception {

        List<JSONObject> spotifyItemIds = trackIds.stream()
                .map(trackId -> new JSONObject().put("uri", generateSpotifyId(trackId)))
                .collect(Collectors.toList());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks"))
                .header("Authorization", "Bearer " + accessToken)
                .method("DELETE", HttpRequest.BodyPublishers.ofString(
                        new JSONObject().put("tracks", spotifyItemIds)
                                .toString()
                        )
                ).build();

        HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }

    public void addTracksToPlaylist(String accessToken,String playlistId, List<String> trackIds) throws Exception {
        String[] trackIdsArray = trackIds
                .stream()
                .map(this::generateSpotifyId)
                .collect(Collectors.toList())
                .toArray(new String[trackIds.size()]);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/playlists/" + playlistId + "/tracks"))
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(
                        new JSONObject().put("uris", trackIdsArray)
                                .toString()
                        )
                )
                .build();
        HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    public Track loadSpotifyTrack(String accessToken, Track.TrackDto trackDto) throws Exception {
        String url = new Util.UrlBuilder("https://api.spotify.com/v1/search")
                .withParams("query", URLEncoder.encode(trackDto.getName() + " " + trackDto.getArtist(), StandardCharsets.UTF_8))
                .withParams("type", URLEncoder.encode("track,artist", StandardCharsets.UTF_8))
                .withParams("limit", "1")
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(url))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray trackJsonArray = new JSONObject(response.body())
                .getJSONObject("tracks")
                .getJSONArray("items");

        if (trackJsonArray.length() < 1) {
            return null;
        }
        JSONObject trackJson = trackJsonArray.getJSONObject(0);
        return new Track(
                trackJson.getString("id"),
                trackJson.getString("name"),
                trackJson.getJSONArray("artists")
                        .getJSONObject(0)
                        .getString("name")
        );
    }

    private String generateSpotifyId(String trackId) {
        return "spotify:track:" + trackId;
    }

    public static class FetchItemsParams {
        @Min(0) @Max(15)
        final Integer limit;

        @Min(0) @Max(49)
        final Integer offset;

        public FetchItemsParams(Integer limit, Integer offset) {
            this.limit = limit;
            this.offset = offset;
        }
    }

    public static class FetchRecentlyPlayedParams extends FetchItemsParams {
        Integer after;
        Integer before;

        public FetchRecentlyPlayedParams(Integer limit, Integer offset, Integer after, Integer before) {
            super(limit, offset);
            this.after = after;
            this.before = before;
        }
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

        @Min(0) @Max(49)
        private Integer offset;
    }

    @ToString
    @Getter @Setter
    @AllArgsConstructor
    public static class CreatePlaylistRequestBody {
        @NotNull
        private String name;

        @NotNull
        private String description;

        @NotNull
        private boolean _public;
    }

}
