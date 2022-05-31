package kara.spotifyassistant.events;

import kara.spotifyassistant.appuser.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ApiEvent extends ApplicationEvent {
    private final String message;
    private final AppUser payload;

    public ApiEvent(Object source, String message, AppUser payload) {
        super(source);
        this.message = message;
        this.payload = payload;
    }
}
