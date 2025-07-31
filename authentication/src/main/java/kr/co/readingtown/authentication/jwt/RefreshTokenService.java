package kr.co.readingtown.authentication.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REFRESH_KEY_PREFIX = "refresh:";

    private String generateRefreshTokenKey(String provider, String providerId) {
        return REFRESH_KEY_PREFIX + provider + ":" + providerId;
    }

    public void saveRefreshToken(String provider, String providerId, String token, long ttl) {
        String key = generateRefreshTokenKey(provider, providerId);
        redisTemplate.opsForValue().set(key, token, ttl, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String provider, String providerId) {
        String key = generateRefreshTokenKey(provider, providerId);
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteRefreshToken(String provider, String providerId) {
        String key = generateRefreshTokenKey(provider, providerId);
        redisTemplate.delete(key);
    }
}
