package pl.cinkus.IdentityService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.cinkus.backend.codegen.types.UserRole;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    private UUID id;
    private String name;
    private String surname;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(unique = true)
    private String nickName;

    @Column(unique = true)
    private String email;
    private String password;
    private String registrationCode;
    private boolean verified = false;
}
