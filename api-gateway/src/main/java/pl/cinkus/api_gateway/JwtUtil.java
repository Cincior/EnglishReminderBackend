package pl.cinkus.api_gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;

    //ToDo Move this property into a separate property class
    public JwtUtil(
            @Value("${application.security.jwt.secret-key}") String secretKey
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateAndParse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date now = Date.from(Instant.now());

            if (claims.getExpiration() == null
                    || claims.getExpiration().before(now)
                    || claims.getIssuedAt().after(now)
                    || claims.get("role", String.class) == null) {
                return null;
            }

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }

    }
}
