package pl.cinkus.IdentityService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.cinkus.IdentityService.entity.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
