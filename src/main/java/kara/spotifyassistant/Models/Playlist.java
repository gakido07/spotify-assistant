package kara.spotifyassistant.Models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Playlist {
    private String id;
    private String[] trackIds;

    public Playlist(String id) {
        this.id = id;
        this.trackIds = null;
    }
}
