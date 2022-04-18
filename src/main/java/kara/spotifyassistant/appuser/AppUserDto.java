package kara.spotifyassistant.appuser;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AppUserDto {

    private String clientId;

    private String apiKey;

    public AppUserDto(String clientId, String apiKey) {
        this.clientId = clientId;
        this.apiKey = apiKey;
    }
}
