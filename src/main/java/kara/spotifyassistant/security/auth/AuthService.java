package kara.spotifyassistant.security.auth;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.security.SecurityUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private AppUserService appUserService;
    private SecurityUtil securityUtil;


    public AuthService(AppUserService appUserService, SecurityUtil securityUtil) {
        this.appUserService = appUserService;
        this.securityUtil = securityUtil;
    }


}
