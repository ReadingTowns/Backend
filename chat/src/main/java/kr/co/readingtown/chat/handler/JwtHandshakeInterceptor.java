package kr.co.readingtown.chat.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.readingtown.chat.integration.member.MemberClient;
import kr.co.readingtown.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.security.Key;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${jwt.secret}")
    private String secret;

    private Key key;
    private final MemberClient memberClient;
    private final CookieUtil cookieUtil;
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

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

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // WebSocket 쿼리 파라미터에서 roomId 추출
            String query = request.getURI().getQuery();
            if (query != null) {
                String roomId = extractQueryParam(query, "roomId");
                if (roomId != null && !roomId.isEmpty()) {
                    attributes.put("roomId", roomId);
                }
            }

            String token = cookieUtil.extractTokenFromCookie(httpRequest, ACCESS_TOKEN_COOKIE_NAME);

            if (token == null) {
                return false;
            }

            try {
                Claims claims = parseClaims(token).getBody();
                String provider = claims.get("provider", String.class);
                String providerId = claims.getSubject();
                Long memberId = memberClient.getMemberId(provider, providerId);

                attributes.put("memberId", memberId);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
    }

    /**
     * 쿼리 스트링에서 특정 파라미터 값 추출
     * 예: "roomId=123&other=value" -> "123"
     */
    private String extractQueryParam(String query, String paramName) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        String[] params = query.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(paramName)) {
                return keyValue[1];
            }
        }
        return null;
    }
}
