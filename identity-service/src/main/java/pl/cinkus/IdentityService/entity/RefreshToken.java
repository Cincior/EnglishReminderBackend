package pl.cinkus.IdentityService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class RefreshToken {
    @Id
    private UUID refreshTokenId;
    private UUID ownerId;
    @Column(unique = true)
    private String refreshToken;
    private Date expirationDate;
}
