package kr.co.readingtown.authentication.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_VALUE = "logout";
    private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

    private String generateBlacklistKey (String token) {
        return BLACKLIST_KEY_PREFIX + token;
    }

    public void addToBlacklist(String token, long expirationMillis) {
        String blacklistKey = generateBlacklistKey(token);
        redisTemplate.opsForValue().set(blacklistKey, BLACKLIST_VALUE, expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isBlacklisted(String token) {
        String blacklistKey = generateBlacklistKey(token);
        return Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey));
    }
}
