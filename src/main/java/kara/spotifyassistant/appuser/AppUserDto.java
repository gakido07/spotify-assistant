package kara.spotifyassistant.appuser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class AppUserDto {
    private String clientId;
    private String publickKey;
    private String privateKey;
}
