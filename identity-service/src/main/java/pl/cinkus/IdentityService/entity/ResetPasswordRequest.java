package pl.cinkus.IdentityService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reset_password_requests")
public class ResetPasswordRequest {
    @Id
    @Column(nullable = false, length = 254)
    private String email;

    @Column(name = "reset_code", nullable = false, length = 6)
    private String resetCode;
}
