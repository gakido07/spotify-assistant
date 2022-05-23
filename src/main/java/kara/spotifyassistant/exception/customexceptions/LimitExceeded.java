package kara.spotifyassistant.exception.customexceptions;

public class LimitExceeded extends RuntimeException {
    public LimitExceeded(String message) {
        super(message);
    }
}
