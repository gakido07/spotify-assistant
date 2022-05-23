package kara.spotifyassistant.config;

import kara.spotifyassistant.apiwrappers.SpotifyApiWrapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private SpotifyApiWrapper spotifyApiWrapper;

    public SchedulerConfig(SpotifyApiWrapper spotifyApiWrapper) {
        this.spotifyApiWrapper = spotifyApiWrapper;
    }

    @Scheduled(cron = "0 0 1 * * MON")
    public void schedulePlaylistCreation() {
        System.out.println("I am Invincible");
    }
}
