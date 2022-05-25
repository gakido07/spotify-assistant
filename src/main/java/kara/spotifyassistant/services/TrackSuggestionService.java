package kara.spotifyassistant.services;

import kara.spotifyassistant.Models.Playlist;
import kara.spotifyassistant.Models.Track;
import kara.spotifyassistant.apiwrappers.LastFmApiWrapper;
import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.config.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class TrackSuggestionService {

    public static String SUGGESTION_PLAYLIST_NAME = "S:A Suggestions";

    private final SpotifyApiWrapper spotifyApiWrapper;
    private final LastFmApiWrapper lastFmApiWrapper;
    private final AppUserService appUserService;

    public TrackSuggestionService(
            SpotifyApiWrapper spotifyApiWrapper,
            LastFmApiWrapper lastFmApiWrapper,
            AppUserService appUserService
    ) {
        this.spotifyApiWrapper = spotifyApiWrapper;
        this.lastFmApiWrapper = lastFmApiWrapper;
        this.appUserService = appUserService;
    }

    public void testCron() throws Exception {
        List<AppUser> users = appUserService.getAllAppUsers();
        for (AppUser user : users) {
            if (user.getSuggestionPlaylistId() == null) {
                String playlistId = spotifyApiWrapper.createPlaylist(user.getId(), new SpotifyApiWrapper.CreatePlaylistRequestBody(
                        SUGGESTION_PLAYLIST_NAME,
                        LocalDate.now().toString(),
                        false
                ));
                user.setSuggestionPlaylistId(playlistId);
                appUserService.saveAppUser(user);
            }
            else {
                JSONArray playlistTracks = spotifyApiWrapper.getPlaylistTracks(user.getId(), user.getSuggestionPlaylistId());
                if (playlistTracks.length() > 0) {
                    List<JSONObject> trackIdsJson = Util.convertJsonArrayToList(playlistTracks);
                    List<String> trackIds = trackIdsJson
                            .stream()
                            .map(trackId -> trackId.getJSONObject("track").getString("id"))
                            .collect(Collectors.toList());
                    spotifyApiWrapper.deleteTracksFromPlaylist(user.getId(), user.getSuggestionPlaylistId(), trackIds);
                }
            }
            suggestPlaylist(user);
        }
    }


    public Object suggestPlaylist(AppUser user) throws Exception {
        String token = spotifyApiWrapper.fetchAccessToken(user.getId());
        List<Track.TrackDto> tracks = getSampleFromTopTracks(user);
        analyseAndSuggestTracks(tracks, token , user);
        return null;
    }

    private void analyseAndSuggestTracks(List<Track.TrackDto> tracks, String spotifyToken, AppUser appUser) {
        HttpClient client = HttpClient.newHttpClient();
        List<URI> targets = tracks.stream().map(
                track -> {
                    try {
                        String url = lastFmApiWrapper.buildUrlForGettingSimilarTracks(track);
                        return new URI(url);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    throw new IllegalArgumentException("Invalid data passed");
                }).collect(Collectors.toList());

        targets.stream().forEach(
                target -> client.sendAsync(
                        HttpRequest.newBuilder(target)
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                ).thenApply(response -> {
                    JSONObject responseJson = new JSONObject(response.body());
                    JSONArray similarTracks = responseJson
                            .getJSONObject("similartracks")
                            .getJSONArray("track");
                    if(similarTracks.length() == 0) {
                        return responseJson;
                    }
                    List<String> trackIds = new ArrayList<>();
                    for (int count = 0; count < similarTracks.length(); count++) {
                        JSONObject trackJson = similarTracks.getJSONObject(count);
                        Track.TrackDto trackDto = new Track.TrackDto(
                                trackJson.getString("name"),
                                trackJson.getJSONObject("artist").getString("name")
                        );
                        try {
                            Track track = spotifyApiWrapper.loadSpotifyTrack(appUser.getId(), trackDto);
                            trackIds.add(track.getId());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                    try {
                        spotifyApiWrapper.addTracksToPlaylist(appUser.getId(), appUser.getSuggestionPlaylistId(), trackIds);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return responseJson;
                })
        );

    }

    private List<Track.TrackDto> getSampleFromTopTracks(AppUser appUser) throws Exception {
        List<Track.TrackDto> sampleTopTracks = new ArrayList<>();
        JSONArray topTracks = spotifyApiWrapper.getTopItems(appUser.getId(),
                new SpotifyApiWrapper.GetTopItemsRequestParams(
                        SpotifyApiWrapper.ITEM_TYPE.tracks,
                        SpotifyApiWrapper.TIME_RANGE.long_term,
                        15,
                        Util.generateRandomNumber(0,30)
                )).getJSONArray("items");
        topTracks.iterator().forEachRemaining(track -> {
            JSONObject jsonTrack = (JSONObject) track;
            String artist = jsonTrack.getJSONArray("artists")
                    .getJSONObject(0)
                    .getString("name");
            String trackName = jsonTrack.getString("name");
            sampleTopTracks.add(new Track.TrackDto(trackName, artist));
        });
        return sampleTopTracks;
    }
}
