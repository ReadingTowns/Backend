package kr.co.readingtown.authentication.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveRefreshToken(String provider, String providerId, String token, long ttl) {
        String key = "refresh:" + provider + ":" + providerId;
        redisTemplate.opsForValue().set(key, token, ttl, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String provider, String providerId) {
        return redisTemplate.opsForValue().get("refresh:" + provider + ":" + providerId);
    }

    public void deleteRefreshToken(String provider, String providerId) {
        redisTemplate.delete("refresh:" + provider + ":" + providerId);
    }
}
