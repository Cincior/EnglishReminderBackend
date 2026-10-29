package pl.cinkus.IdentityService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_tokens_owner_id", columnList = "owner_id"),
        }
)
public class RefreshToken {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID refreshTokenId;

    @Column(name = "owner_id", nullable = false, updatable = false)
    private UUID ownerId;

    @Column(nullable = false, unique = true, length = 36, updatable = false)
    private String refreshToken;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;
}
