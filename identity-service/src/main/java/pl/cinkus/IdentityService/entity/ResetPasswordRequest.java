package pl.cinkus.IdentityService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@Data
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @Id
    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String resetCode;
}
