package pl.cinkus.IdentityService.service;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.cinkus.IdentityService.entity.RefreshToken;
import pl.cinkus.IdentityService.exception.ErrorCode;
import pl.cinkus.IdentityService.exception.IdentityServiceException;
import pl.cinkus.IdentityService.model.UserRole;
import pl.cinkus.IdentityService.repository.RefreshTokenRepository;
import pl.cinkus.IdentityService.util.JwtProperties;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken(UUID ownerId) {
        UUID refreshTokenId = UUID.randomUUID();
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshTokenId(refreshTokenId)
                .ownerId(ownerId)
                .refreshToken(refreshTokenId.toString())
                .expirationDate(Date.from(Instant.now().plus(jwtProperties.getRefreshExpiration())))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public void verifyExpiration(RefreshToken refreshToken) {
        if(refreshToken.getExpirationDate().before(Date.from(Instant.now()))) {
            refreshTokenRepository.delete(refreshToken);
            throw new IdentityServiceException(ErrorCode.REFRESH_TOKEN_EXPIRED, "Refresh token has expired");
        }
    }
}
