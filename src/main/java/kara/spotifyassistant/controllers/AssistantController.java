package kara.spotifyassistant.controllers;

import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import kara.spotifyassistant.services.AppUserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/{id}")
public class AssistantController {

  private final SpotifyApiWrapper spotifyApiWrapper;
  private final AppUserService appUserService;


  public AssistantController(SpotifyApiWrapper spotifyApiWrapper, AppUserService appUserService) {
    this.spotifyApiWrapper = spotifyApiWrapper;
    this.appUserService = appUserService;
  }

  @GetMapping(value = "/top-items", produces = MediaType.APPLICATION_JSON_VALUE)
  public Object getTopItems(@PathVariable("id") String id, @Valid SpotifyApiWrapper.GetTopItemsRequestParams requestParams) throws Exception {
    String accessToken = appUserService.getAccessToken(id);
    return spotifyApiWrapper.getTopItems(accessToken, requestParams).toString();
  }

  @GetMapping(path = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
  public Object getUserSpotifyProfile(@PathVariable("id") String id) throws Exception {
    String accessToken = appUserService.getAccessToken(id);
    return appUserService.fetchUserSpotifyProfile(accessToken).toString();
  }

  @GetMapping(path = "/recently-played", produces = MediaType.APPLICATION_JSON_VALUE)
  public Object getUserRecentlyPlayed(
      @PathVariable("id") String id,
      @Valid SpotifyApiWrapper.FetchRecentlyPlayedParams params
  ) throws Exception {
    String accessToken = appUserService.getAccessToken(id);
    return spotifyApiWrapper.getUserRecentlyPlayed(accessToken, params).toString();
  }

  @GetMapping(value = "/current-track", produces = MediaType.APPLICATION_JSON_VALUE)
  public Object getCurrentlyPlayingTrack(@PathVariable("id") String id) throws Exception {
    String accessToken = appUserService.getAccessToken(id);
    return spotifyApiWrapper.getCurrentTrack(accessToken).toString();
  }

  @GetMapping(value = "/playlist/{playlistId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Object getPlaylistTracks(@PathVariable("id") String id, @PathVariable("playlistId") String playlistId) throws Exception {
    String accessToken = appUserService.getAccessToken(id);
    return spotifyApiWrapper.getPlaylistTracks(accessToken, playlistId).toString();
  }
}
