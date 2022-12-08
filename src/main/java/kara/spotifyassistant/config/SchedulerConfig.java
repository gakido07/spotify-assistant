package kara.spotifyassistant.config;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.services.AppUserService;
import kara.spotifyassistant.services.TrackSuggestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    private final TrackSuggestionService trackSuggestionService;
    private final AppUserService appUserService;

    @Autowired
    public SchedulerConfig(TrackSuggestionService trackSuggestionService, AppUserService appUserService) {
        this.trackSuggestionService = trackSuggestionService;
        this.appUserService = appUserService;
    }

    @Scheduled(cron = "0 0 1 * * MON")
    public void schedulePlaylistCreation() throws Exception {
        log.info("Running suggestion cron");
        List<AppUser> users = appUserService.getAllAppUsers();
        for (AppUser user : users) {
            trackSuggestionService.initializeSuggestionPlaylist(user);
            trackSuggestionService.suggestPlaylist(user);
        }
    }
}
