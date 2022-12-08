package kara.spotifyassistant.Models;

import kara.spotifyassistant.appuser.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final AppUser payload;

    public UserRegisteredEvent(Object source, AppUser payload) {
        super(source);
        this.payload = payload;
    }
}
