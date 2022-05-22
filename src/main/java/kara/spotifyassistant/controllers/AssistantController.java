package kara.spotifyassistant.controllers;

import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/{id}")
public class AssistantController {

    private SpotifyApiWrapper spotifyApiWrapper;

    public AssistantController(SpotifyApiWrapper spotifyApiWrapper) {
        this.spotifyApiWrapper = spotifyApiWrapper;
    }

    @GetMapping(value = "/top-items", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getTopItems(@PathVariable("id") String clientId, @RequestParam SpotifyApiWrapper.ITEM_TYPE itemType) throws Exception {
        return spotifyApiWrapper.getTopItems(clientId, itemType).toString();
    }

    @GetMapping(value = "/current-track", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getCurrentlyPlayingTrack(@PathVariable("id") String clientId) throws Exception {
        return spotifyApiWrapper.getCurrentTrack(clientId).toString();
    }
}
