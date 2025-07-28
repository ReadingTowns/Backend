package kr.co.readingtown.authentication.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;
    private final long accessTokenValidityInMillis = 1000 * 60 * 15; // 15분
    private final long refreshTokenValidityInMillis = 1000L * 60 * 60 * 24 * 7; // 일주일

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    private String createToken(String provider, String providerId, String type, long validity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validity);

        return Jwts.builder()
                .setSubject(providerId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("provider", provider)
                .claim("type", type)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createAccessToken(String provider, String providerId) {

        return createToken(provider, providerId, "access", accessTokenValidityInMillis);
    }

    public String createRefreshToken(String provider, String providerId) {

        return createToken(provider, providerId, "refresh", refreshTokenValidityInMillis);
    }

    public String getProvider(String token) {
        return parseClaims(token).getBody().get("provider", String.class);
    }

    public String getProviderId(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public String getTokenType(String token) {
        return parseClaims(token).getBody().get("type", String.class);
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
        catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
