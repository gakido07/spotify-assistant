package kara.spotifyassistant.controllers;

import kara.spotifyassistant.appuser.AppUser;
import kara.spotifyassistant.appuser.AppUserDto;
import kara.spotifyassistant.appuser.AppUserRegistrationDetails;
import kara.spotifyassistant.appuser.AppUserService;
import kara.spotifyassistant.services.TrackSuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "/auth")
public class AuthController {

    @Value("${spotify.client.id}")
    private String spotifyClientId;

    @Value("${spotify.client.secret}")
    private String spotifyClientSecret;

    private final AppUserService appUserService;
    private final TrackSuggestionService suggestionService;

    @Autowired
    public AuthController(AppUserService appUserService, TrackSuggestionService suggestionService) {
        this.appUserService = appUserService;
        this.suggestionService = suggestionService;
    }

    @GetMapping("/spotify-auth")
    public ModelAndView getHome() {
        return new ModelAndView("redirect:"
                +
                "https://accounts.spotify.com/en/authorize?response_type=code&client_id="
                + spotifyClientId
                + "&scope=user-read-private%20user-top-read%20user-read-email%20playlist-modify-private%20playlist-read-private%20playlist-modify-public%20user-read-playback-state%20user-library-read&redirect_uri=http://localhost:8080/auth/register&state=efrtyubnghjikopg"
        );
    }

    @GetMapping( path = "/register")
    public String register(@RequestParam String code, Model model) throws Exception {
        AppUserRegistrationDetails registrationDetails = appUserService.registerUser(code);
        suggestionService.initializeSuggestionPlaylist(registrationDetails.getAppUser());
        suggestionService.suggestPlaylist(registrationDetails.getAppUser());
        model.addAttribute("appUserDto", registrationDetails.getAppUserDto());
        return "user-account-details";
    }
}