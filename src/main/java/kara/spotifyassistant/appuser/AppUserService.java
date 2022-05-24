package kara.spotifyassistant.appuser;

import kara.spotifyassistant.exception.customexceptions.UserNotFound;
import kara.spotifyassistant.security.SecurityUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    private AppUserRepository appUserRepository;
    private final SecurityUtil securityUtil;


    @Autowired
    public AppUserService(AppUserRepository appUserRepository, SecurityUtil securityUtil) {
        this.appUserRepository = appUserRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return appUserRepository.findById(id).orElseThrow(() -> new UserNotFound("User not found"));
    }

    public AppUser findUserById(String id) throws Exception {
        return appUserRepository.findById(id).orElseThrow(() -> new UserNotFound("User not found"));
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

    public AppUserDto registerUser(String code) throws Exception {
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
        JSONObject profile = fetchUserSpotifyProfile(accessToken);
        AppUser appUser = new AppUser(refreshToken, profile.get("email").toString(), profile.getString("id"));
        String unhashedPublicKey = appUser.getPublicKey();
        String unhashedPrivateKey = appUser.getPrivateKey();
        appUser.setPublicKey(securityUtil.BcryptEncoder().encode(unhashedPublicKey));
        appUser.setPrivateKey(securityUtil.BcryptEncoder().encode(unhashedPrivateKey));
        saveAppUser(appUser);
        return new AppUserDto(appUser.getId(), unhashedPublicKey, unhashedPrivateKey);
    }
}
