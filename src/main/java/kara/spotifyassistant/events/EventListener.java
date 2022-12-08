package kara.spotifyassistant.events;

import kara.spotifyassistant.Models.UserRegisteredEvent;
import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.services.AppUserService;
import kara.spotifyassistant.services.TrackSuggestionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EventListener implements ApplicationListener<UserRegisteredEvent> {
    private final TrackSuggestionService trackSuggestionService;

    @Autowired
    public EventListener(TrackSuggestionService trackSuggestionService, AppUserService appUserService) {
        this.trackSuggestionService = trackSuggestionService;
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(UserRegisteredEvent event) {
        log.info("handled");
        AppUser user = event.getPayload();
        trackSuggestionService.initializeSuggestionPlaylist(user);
        trackSuggestionService.suggestPlaylist(user);
    }
}
