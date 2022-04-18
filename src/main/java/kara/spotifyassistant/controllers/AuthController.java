package kara.spotifyassistant.controllers;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserDto;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.security.SecurityUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    private final AppUserService appUserService;
    private final SecurityUtil securityUtil;

    @Autowired
    public AuthController(AppUserService appUserService, SecurityUtil securityUtil) {
        this.appUserService = appUserService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/spotify-auth")
    public ModelAndView getHome() {
        return new ModelAndView("redirect:" + "https://accounts.spotify.com/en/authorize?response_type=code&client_id=" + spotifyClientId + "&scope=user-read-private%20user-top-read%20user-read-email%20playlist-modify-private%20playlist-read-private%20playlist-modify-public%20user-read-playback-state%20user-library-read&redirect_uri=http://localhost:8080/register&state=efrtyubnghjikopg");
    }


    @GetMapping( path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUserDto> register(@RequestParam String code) throws URISyntaxException, IOException, InterruptedException {
        var form = new HashMap<String, String>() {{
            put("code", code);
            put ("redirect_uri", "http://localhost:8080/register");
            put("grant_type", "authorization_code");
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

        JSONObject responseObject = new JSONObject(response.body());
        String accessToken = responseObject.getString("access_token");
        String refreshToken = responseObject.getString("refresh_token");
        JSONObject profile = appUserService.fetchUserSpotifyProfile(accessToken);
        AppUser appUser = new AppUser(refreshToken, profile.get("email").toString());
        String unhashedApiKey = appUser.getApiKey();
        appUser.setApiKey(securityUtil.BcryptEncoder().encode(unhashedApiKey));
        appUserService.saveAppUser(appUser);
        return new ResponseEntity<>(
                new AppUserDto(appUser.getId(), unhashedApiKey),
                HttpStatus.OK
        );
    }
}