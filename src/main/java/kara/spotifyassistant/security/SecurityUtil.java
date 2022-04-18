package kara.spotifyassistant.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Base64;

@Configuration
public class SecurityUtil {

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    public PasswordEncoder BcryptEncoder(){
        return new BCryptPasswordEncoder();
    }

    public String extractClientIdFromRoute(String route) {
        return route.split("/")[1];
    }

    public String getEncodedAuthHeader() {
        return Base64.getEncoder().encodeToString((spotifyClientId + ":" + spotifyClientSecret).getBytes());
    }

}
