package kara.spotifyassistant.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Getter
@NoArgsConstructor
public class SpotifyToken {
    private String value;
    private Date timeGenerated;

    public SpotifyToken(String token) {
        this.value = token;
        this.timeGenerated = new Date();
    }

    public boolean isTokenValid() {
        long diff = Math.abs(new Date().getTime() - this.timeGenerated.getTime());

        long duration = TimeUnit.HOURS.convert(
                diff,
                TimeUnit.MILLISECONDS
        );
        return duration < 1;
    }
}
