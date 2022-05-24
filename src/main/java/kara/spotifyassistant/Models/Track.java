package kara.spotifyassistant.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter @Setter
@ToString
public class Track {
    private String id;
    private String name;
    private String artist;

    @AllArgsConstructor
    @Getter @Setter @ToString
    public static class TrackDto {
        private String name;
        private String artist;
    }
}
