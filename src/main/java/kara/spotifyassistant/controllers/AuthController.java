package kara.spotifyassistant.controllers;

import io.jsonwebtoken.SignatureAlgorithm;
import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserDto;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.security.SecurityUtil;
import org.apache.tomcat.util.security.MD5Encoder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmConstraints;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    private final AppUserService appUserService;

    @Autowired
    public AuthController(AppUserService appUserService, SecurityUtil securityUtil) {
        this.appUserService = appUserService;
    }

    @GetMapping("/spotify-auth")
    public ModelAndView getHome() {
        return new ModelAndView("redirect:" + "https://accounts.spotify.com/en/authorize?response_type=code&client_id=" + spotifyClientId + "&scope=user-read-private%20user-top-read%20user-read-email%20playlist-modify-private%20playlist-read-private%20playlist-modify-public%20user-read-playback-state%20user-library-read&redirect_uri=http://localhost:8080/register&state=efrtyubnghjikopg");
    }

    @GetMapping( path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppUserDto> register(@RequestParam String code) throws Exception {
        AppUserDto appUserDto = appUserService.registerUser(code);
        return new ResponseEntity<>(
                appUserDto,
                HttpStatus.OK
        );
    }
}