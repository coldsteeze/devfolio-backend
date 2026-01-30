package korobkin.nikita.auth_service.service.impl;

import korobkin.nikita.auth_service.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCacheServiceImpl implements TokenCacheService {

    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_PREFIX = "refresh:";

    @Override
    public void saveRefreshToken(UUID userId, String refreshToken, long ttlDays) {
        redisTemplate.opsForValue().set(
                REFRESH_PREFIX + userId,
                refreshToken,
                ttlDays,
                TimeUnit.DAYS
        );

        log.info("Saved refresh token for userId={} with TTL={} days", userId, ttlDays);
    }

    @Override
    public String getRefreshToken(UUID userId) {
        String key = REFRESH_PREFIX + userId;
        String token = redisTemplate.opsForValue().get(key);

        if (token == null) {
            log.warn("No refresh token found in Redis for userId={}", userId);
        } else {
            log.debug("Found refresh token in Redis for userId={}", userId);
        }

        return token;
    }

    @Override
    public void deleteRefreshToken(UUID userId) {
        String key = REFRESH_PREFIX + userId;
        Boolean result = redisTemplate.delete(key);

        if (result) {
            log.info("Deleted refresh token from Redis for userId={}", userId);
        } else {
            log.warn("No refresh token to delete for userId={}", userId);
        }
    }
}
