package pl.cinkus.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.cinkus.backend.entity.WordData;
import pl.cinkus.backend.repository.WordDataRepository;

import java.util.UUID;

import static java.time.LocalDateTime.*;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}
}
