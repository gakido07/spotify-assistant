package kara.spotifyassistant.controllers;

import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.security.SecurityUtil;
import kara.spotifyassistant.services.TrackSuggestionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/{id}")
@CrossOrigin(origins = {"http://localhost:3000/"})
public class AssistantController {

    private final SpotifyApiWrapper spotifyApiWrapper;
    private final TrackSuggestionService suggestionService;
    private final SecurityUtil securityUtil;
    private final AppUserService appUserService;


    public AssistantController(SpotifyApiWrapper spotifyApiWrapper, TrackSuggestionService suggestionService, SecurityUtil securityUtil, AppUserService appUserService) {
        this.spotifyApiWrapper = spotifyApiWrapper;
        this.suggestionService = suggestionService;
        this.securityUtil = securityUtil;
        this.appUserService = appUserService;
    }

    @GetMapping(value = "/top-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getTopItems(@PathVariable("id") String id, @Valid SpotifyApiWrapper.GetTopItemsRequestParams requestParams) throws Exception {
        return spotifyApiWrapper.getTopItems(id, requestParams).toString();
    }

    @GetMapping(path = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getUserSpotifyProfile(@PathVariable("id") String id) throws Exception {
        String accessToken = spotifyApiWrapper.fetchAccessToken(id);
        return appUserService.fetchUserSpotifyProfile(accessToken).toString();
    }

    @GetMapping(value = "/current-track", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getCurrentlyPlayingTrack(@PathVariable("id") String clientId) throws Exception {
        return spotifyApiWrapper.getCurrentTrack(clientId).toString();
    }

    @PostMapping(path ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createPlaylistTest(@PathVariable("id") String clientId,
            @RequestBody @Valid SpotifyApiWrapper.CreatePlaylistRequestBody requestBody
    ) throws Exception {
        return spotifyApiWrapper.createPlaylist(clientId, requestBody);
    }
}
