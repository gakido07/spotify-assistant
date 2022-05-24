package kara.spotifyassistant.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class SpotifyToken {
    private String value;
    private Date timeGenerated;

    public SpotifyToken(String token) {
        this.value = token;
        this.timeGenerated = new Date();
    }
}
