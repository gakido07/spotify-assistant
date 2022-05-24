package kara.spotifyassistant.exception.customexceptions;

public class LimitExceeded extends IllegalArgumentException {
    public LimitExceeded(String message) {
        super(message);
    }
}
