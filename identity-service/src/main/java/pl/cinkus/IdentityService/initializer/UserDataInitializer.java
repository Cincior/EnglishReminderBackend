package pl.cinkus.IdentityService.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.cinkus.IdentityService.entity.User;
import pl.cinkus.IdentityService.repository.UserRepository;

import java.util.UUID;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class UserDataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final DefaultUserProperties defaultUserProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if( defaultUserProperties.getEmail() == null ||
                defaultUserProperties.getRole() == null ||
                defaultUserProperties.getName() == null ||
                defaultUserProperties.getSurname() == null ||
                defaultUserProperties.getNickname() == null ||
                defaultUserProperties.getPassword() == null
        ) {
            log.info("Admin could not be created. At least 1 prop is null");
            return;
        }

        if(userRepository.existsByEmail(defaultUserProperties.getEmail())) {
            return;
        }

        userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name(defaultUserProperties.getName())
                .surname(defaultUserProperties.getSurname())
                .nickName(defaultUserProperties.getNickname())
                .email(defaultUserProperties.getEmail())
                .password(passwordEncoder.encode(defaultUserProperties.getPassword()))
                .role(defaultUserProperties.getRole())
                .verified(true)
                .build());
    }
}
