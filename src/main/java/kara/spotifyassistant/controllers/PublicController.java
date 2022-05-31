package kara.spotifyassistant.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/public")
public class PublicController {
    @GetMapping(path = "/docs")
    public String docs(Model model) {
        return "docs";
    }
}
