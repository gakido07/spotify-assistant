package kara.spotifyassistant.controllers;

import kara.spotifyassistant.Models.UserRegisteredEvent;
import kara.spotifyassistant.appuser.AppUserRegistrationDetails;
import kara.spotifyassistant.services.AppUserService;
import kara.spotifyassistant.events.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(path = "/auth")
@Slf4j
public class AuthController {

    @Value("${spotify.client.id}")
    private String spotifyClientId;
    @Value("${spotify.redirect.uri}")
    private String spotifyRedirectUri;
    private final AppUserService appUserService;
    private final EventPublisher eventPublisher;

    @Autowired
    public AuthController(AppUserService appUserService, EventPublisher eventPublisher) {
        this.appUserService = appUserService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/spotify-auth")
    public ModelAndView getHome() {
        return new ModelAndView("redirect:"
                +
                "https://accounts.spotify.com/en/authorize?response_type=code&client_id="
                + spotifyClientId
                + "&scope=user-read-private%20user-top-read%20user-read-email%20playlist-modify-private%20playlist-read-private%20playlist-modify-public%20user-read-recently-played%20user-read-playback-state%20user-library-read&redirect_uri=" + spotifyRedirectUri + "&state=" + RandomStringUtils.random(16)
        );
    }

    @GetMapping( path = "/register")
    public String register(@RequestParam String code, Model model) throws Exception {
        try {
            AppUserRegistrationDetails registrationDetails = appUserService.registerUser(code);
            eventPublisher.publishCustomEvent(new UserRegisteredEvent(this.getClass().getSimpleName(), registrationDetails.getAppUser()));
            model.addAttribute("appUserDto", registrationDetails.getAppUserDto());
            return "user-account-details";
        }
        catch (Exception exception) {
            exception.printStackTrace();
            String message = exception.getClass().getSimpleName().equals("DuplicateKeyException") ?
                    "You're already registered" : "An error occurred";
            model.addAttribute("message", message);
            return "error";
        }
    }
}