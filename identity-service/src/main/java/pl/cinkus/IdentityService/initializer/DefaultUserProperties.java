package pl.cinkus.IdentityService.initializer;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.cinkus.IdentityService.model.UserRole;

@Configuration
@ConfigurationProperties(prefix = "application.admin")
@Data
public class DefaultUserProperties {
    private String email;
    private String password;
    private String nickname;
    private String name;
    private String surname;
    private UserRole role;
}
