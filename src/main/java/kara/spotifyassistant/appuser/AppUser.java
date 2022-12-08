package kara.spotifyassistant.appuser;

import kara.spotifyassistant.Models.EncryptedData;
import kara.spotifyassistant.Models.Playlist;
import kara.spotifyassistant.Models.SpotifyToken;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

@Document(collection = "appusers")
@Getter @Setter @ToString
public class AppUser implements UserDetails {

    @Id
    private String id;
    @Email
    @Indexed(unique = true)
    private String email;
    private String spotifyId;
    private String publicKey;
    private String privateKey;
    private EncryptedData refreshToken;
    private SpotifyToken accessToken;
    private String suggestionPlaylistId;

    public AppUser(EncryptedData refreshToken, String email, String spotifyId) {
        super();
        this.email = email;
        this.spotifyId = spotifyId;
        this.publicKey = UUID.randomUUID().toString();
        this.accessToken = null;
        this.privateKey = UUID.randomUUID().toString();
        this.refreshToken = refreshToken;
        this.suggestionPlaylistId = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() { return null;}

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
