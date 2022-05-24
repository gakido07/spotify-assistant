package kara.spotifyassistant.controllers;

import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import kara.spotifyassistant.services.TrackSuggestionService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/{id}")
public class AssistantController {

    private final SpotifyApiWrapper spotifyApiWrapper;
    private final TrackSuggestionService suggestionService;

    public AssistantController(SpotifyApiWrapper spotifyApiWrapper, TrackSuggestionService suggestionService) {
        this.spotifyApiWrapper = spotifyApiWrapper;
        this.suggestionService = suggestionService;
    }

    @GetMapping(value = "/top-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getTopItems(@PathVariable("id") String id, @Valid SpotifyApiWrapper.GetTopItemsRequestParams requestParams) throws Exception {
        return spotifyApiWrapper.getTopItems(id, requestParams).toString();
    }

    @GetMapping(value = "/current-track", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getCurrentlyPlayingTrack(@PathVariable("id") String clientId) throws Exception {
        return spotifyApiWrapper.getCurrentTrack(clientId).toString();
    }

    @PostMapping(path ="/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Object createPlaylistTest(@PathVariable("id") String clientId,
            @RequestBody @Valid SpotifyApiWrapper.CreatePlaylistRequestBody requestBody
    ) throws Exception {
        System.out.println(requestBody.toString());
        return spotifyApiWrapper.createPlaylist(clientId, requestBody).toString();
    }

    @GetMapping(path = "/lol", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object lol() throws Exception {
        return suggestionService.suggestPlaylist().toString();
    }

}
