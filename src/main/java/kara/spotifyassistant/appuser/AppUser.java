package kara.spotifyassistant.appuser;

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
    @Indexed
    private String email;

    private String apiKey;

    private String refreshToken;

    public AppUser(String refreshToken, String email) {
        super();
        this.email = email;
        this.apiKey = UUID.randomUUID().toString();
        this.refreshToken = refreshToken;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() { return null;}

    @Override
    public String getUsername() {
        return id;
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
