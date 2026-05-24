package pl.cinkus.api_gateway;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pl.cinkus.api_gateway.service.RateLimiter;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    public static final String ID_HEADER = "X-UserId";
    public static final String ROLE_HEADER = "X-UserRole";
    public static final String BEARER = "Bearer ";
    public static final String ROLE = "role";

    private final JwtUtil jwtUtil;
    private final RateLimiter rateLimiter;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            String token = getToken(exchange.getRequest());
            log.info("Headery: {}", exchange.getRequest().getHeaders());
            log.info("token: {}", token);

            if(token == null) {
                return authFailed(exchange.getResponse());
            }

            Claims claims = jwtUtil.validateAndParse(token);
            if (claims == null) {
                return authFailed(exchange.getResponse());
            }
            log.info("Claims: {}", claims);

            return rateLimiter.checkLimit(claims.getSubject())
                    .flatMap(isAllowed -> {
                        if (!isAllowed) {
                            return limitExceeded(exchange.getResponse());
                        }

                        ServerHttpRequest request = exchange.getRequest()
                                .mutate()
                                .header(ID_HEADER, claims.getSubject())
                                .header(ROLE_HEADER, claims.get(ROLE, String.class))
                                .build();

                        return chain.filter(exchange.mutate().request(request).build());
                    });
        };
    }

    private String getToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if(authHeader != null && authHeader.startsWith(BEARER)) {
            return authHeader.substring(BEARER.length());
        }

        return null;
    }

    private Mono<Void> authFailed(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    private Mono<Void> limitExceeded(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return response.setComplete();
    }
}
