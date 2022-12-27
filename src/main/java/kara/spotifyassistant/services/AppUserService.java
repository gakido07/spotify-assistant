package kara.spotifyassistant.services;

import kara.spotifyassistant.Models.EncryptedData;
import kara.spotifyassistant.Models.SpotifyToken;
import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserRegistrationDetails;
import kara.spotifyassistant.appuser.AppUserRepository;
import kara.spotifyassistant.security.SecurityUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UserDetailsService {

    @Value("${spotify.redirect.uri}")
    private String spotifyRedirectUri;

    private final AppUserRepository appUserRepository;
    private final SecurityUtil securityUtil;

    private final SpotifyApiWrapper spotifyApiWrapper;


    @Autowired
    public AppUserService(AppUserRepository appUserRepository, SecurityUtil securityUtil, SpotifyApiWrapper spotifyApiWrapper) {
        this.appUserRepository = appUserRepository;
        this.securityUtil = securityUtil;
        this.spotifyApiWrapper = spotifyApiWrapper;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return appUserRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public String getAccessToken(String id) throws Exception {
        AppUser appUser = findUserById(id);
        if ((appUser.getAccessToken() != null) && appUser.getAccessToken().getValue().length() > 0) {
            if (appUser.getAccessToken().isTokenValid()) {
                return appUser.getAccessToken().getValue();
            }
        }
        String accessToken = spotifyApiWrapper.fetchAccessToken(appUser.getRefreshToken());
        appUser.setAccessToken(new SpotifyToken(accessToken));
        appUserRepository.save(appUser);
        return accessToken;
    }

    public AppUser findUserById(String id) throws Exception {
        return appUserRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public AppUser saveAppUser(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    public JSONObject fetchUserSpotifyProfile(String accessToken) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.spotify.com/v1/me"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

    public List<AppUser> getAllAppUsers() {
        return appUserRepository.findAll();
    }

    public AppUserRegistrationDetails registerUser(String code) throws Exception {
        var form = new HashMap<String, String>() {{
            put("code", code);
            put ("redirect_uri", spotifyRedirectUri);
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
        EncryptedData refreshToken = securityUtil.encrypt(
                responseObject.getString("refresh_token")
        );
        JSONObject profile = fetchUserSpotifyProfile(accessToken);
        AppUser appUser = new AppUser(refreshToken, profile.get("email").toString(), profile.getString("id"));
        String unhashedPublicKey = appUser.getPublicKey();
        String unhashedPrivateKey = appUser.getPrivateKey();
        appUser.setPublicKey(securityUtil.BcryptEncoder().encode(unhashedPublicKey));
        appUser.setPrivateKey(securityUtil.BcryptEncoder().encode(unhashedPrivateKey));
        saveAppUser(appUser);
        return new AppUserRegistrationDetails(appUser.getId(), unhashedPublicKey, unhashedPrivateKey, appUser);
    }
}
