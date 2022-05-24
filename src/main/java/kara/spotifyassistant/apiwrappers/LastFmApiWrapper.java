package kara.spotifyassistant.apiwrappers;

import kara.spotifyassistant.Models.Track;
import kara.spotifyassistant.config.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LastFmApiWrapper {

    @Value("${last.fm.api.key}")
    private String lastFmApiKey;

    private enum Method  {
            track
    }

    public String buildUrlForGettingSimilarTracks(Track.TrackDto track) {
        //lol @ function name

        return new Util.UrlBuilder("http://ws.audioscrobbler.com/2.0/")
                .withParams("method", "track.getsimilar")
                .withParams("artist", URLEncoder.encode(track.getArtist(), StandardCharsets.UTF_8))
                .withParams("format", "json")
                .withParams("track", URLEncoder.encode(track.getName(), StandardCharsets.UTF_8))
                .withParams("limit", "10")
                .withParams("api_key", lastFmApiKey)
                .build();
    }

}
