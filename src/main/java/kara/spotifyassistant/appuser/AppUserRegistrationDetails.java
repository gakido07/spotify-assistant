package kara.spotifyassistant.appuser;

import lombok.Getter;

@Getter
public class AppUserRegistrationDetails extends AppUserDto {
    private final AppUser appUser;

    public AppUserRegistrationDetails(String clientId, String publicKey, String privateKey, AppUser appUser) {
        super(clientId, publicKey, privateKey);
        this.appUser = appUser;
    }

    public AppUserDto getAppUserDto() {
        return new AppUserDto(this.getClientId(), this.getPublicKey(), this.getPrivateKey());
    }
}
