package pl.cinkus.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.cinkus.backend.model.WordData;
import pl.cinkus.backend.repository.WordDataRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.time.LocalDateTime.*;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(WordDataRepository repository) {
		return args -> {
			repository.save(WordData.builder()
					.id(UUID.randomUUID())
					.word("Apple")
					.translation("Jabłko")
					.insertDateTime(now())
					.build());

			repository.save(WordData.builder()
					.id(UUID.randomUUID())
					.word("Chleb")
					.translation("Bread")
					.insertDateTime(now())
					.build());
		};
	}

}
