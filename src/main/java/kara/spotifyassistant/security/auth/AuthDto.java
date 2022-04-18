package kara.spotifyassistant.security.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter @Setter
@AllArgsConstructor
public class AuthDto {
    @NotNull
    private String appUserId;
    @NotNull
    private String accessToken;
    @NotNull
    private String refreshToken;


    public static class SignUpRequest {
        private String accessToken;
        private String refreshToken;
    }
}
