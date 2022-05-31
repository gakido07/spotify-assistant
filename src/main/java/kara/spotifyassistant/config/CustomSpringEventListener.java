package kara.spotifyassistant.config;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.events.ApiEvent;
import kara.spotifyassistant.services.TrackSuggestionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CustomSpringEventListener implements ApplicationListener<ApiEvent> {
    private final TrackSuggestionService trackSuggestionService;
    private final AppUserService appUserService;

    @Autowired
    public CustomSpringEventListener(TrackSuggestionService trackSuggestionService, AppUserService appUserService) {
        this.trackSuggestionService = trackSuggestionService;
        this.appUserService = appUserService;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApiEvent event) {
        log.info("handled");
        AppUser user = event.getPayload();
        trackSuggestionService.initializeSuggestionPlaylist(user);
        trackSuggestionService.suggestPlaylist(user);
    }
}
