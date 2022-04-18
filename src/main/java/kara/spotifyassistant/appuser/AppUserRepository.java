package kara.spotifyassistant.appuser;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface AppUserRepository extends MongoRepository<AppUser, String> {
    @Query("{id:'?0'}")
    Optional<AppUser> findAppUserById(String id);
}
