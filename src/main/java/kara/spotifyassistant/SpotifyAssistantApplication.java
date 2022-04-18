package kara.spotifyassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class SpotifyAssistantApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifyAssistantApplication.class, args);
	}

}
