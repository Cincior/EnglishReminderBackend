package pl.cinkus.IdentityService.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.cinkus.IdentityService.model.UserRole;
import pl.cinkus.IdentityService.util.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {
	private final JwtProperties jwtProperties;

	public String generateToken(UUID userID, UserRole userRole) {
        return Jwts.builder()
                .subject(userID.toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtProperties.getExpiration())))
                .claim("role", userRole)
                .signWith(getSecretKey())
                .compact();
	}

	private SecretKey getSecretKey() {
		byte[] key = jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(key);
	}
}
