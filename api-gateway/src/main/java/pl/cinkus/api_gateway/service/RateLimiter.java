package pl.cinkus.api_gateway.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiter {
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private static final Integer TRIES_LIMIT = 30;
    private static final Integer TIME_WINDOW_SECONDS = 60;
    private static final String KEY_PREFIX = "rate_limit:";
    private RedisScript<Long> redisScript;

    @PostConstruct
    public void init() {
        String script =
                "local current = tonumber(redis.call('GET', KEYS[1])) " +
                        "if current and current >= tonumber(ARGV[2]) then " +
                        "    return current + 1 " +
                        "end " +
                        "current = redis.call('INCR', KEYS[1]) " +
                        "if current == 1 then " +
                        "    redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
                        "end " +
                        "return current";

        DefaultRedisScript<Long> defaultScript = new DefaultRedisScript<>();
        defaultScript.setScriptText(script);
        defaultScript.setResultType(Long.class);
        this.redisScript = defaultScript;
    }

    public Mono<Boolean> checkLimit(String userId) {
        return reactiveRedisTemplate.execute(
                        redisScript,
                        List.of(KEY_PREFIX + userId),
                        List.of(String.valueOf(TIME_WINDOW_SECONDS), String.valueOf(TRIES_LIMIT))
                )
                .next()
                .map(count -> count <= TRIES_LIMIT)
                .onErrorResume(e -> {
                    log.error("Redis error: ", e);
                    return Mono.just(true);
                });
    }
}
