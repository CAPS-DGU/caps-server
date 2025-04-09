package kr.dgucaps.caps.global.config.auth.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kr.dgucaps.caps.domain.member.entity.Role;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.UnauthorizedException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Getter
@Component
public class JwtProvider {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    public String getIssueToken(Long memberId, Role role, boolean isAccessToken) {
        long expireTime = isAccessToken ? ACCESS_TOKEN_EXPIRE_TIME : REFRESH_TOKEN_EXPIRE_TIME;
        return generateToken(memberId, role, expireTime);
    }

    public void validateAccessToken(String accessToken) {
        validateToken(accessToken, ErrorCode.EXPIRED_ACCESS_TOKEN, ErrorCode.INVALID_ACCESS_TOKEN_VALUE);
    }

    public void validateRefreshToken(String refreshToken) {
        validateToken(refreshToken, ErrorCode.EXPIRED_REFRESH_TOKEN, ErrorCode.INVALID_REFRESH_TOKEN_VALUE);
    }

    private void validateToken(String token, ErrorCode expiredError, ErrorCode invalidError) {
        try {
            getJwtParser().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(expiredError);
        } catch (Exception e) {
            throw new UnauthorizedException(invalidError);
        }
    }

    public void equalsRefreshToken(String providedRefreshToken, String storedRefreshToken) {
        if (!providedRefreshToken.equals(storedRefreshToken)) {
            throw new UnauthorizedException(ErrorCode.NOT_MATCH_REFRESH_TOKEN);
        }
    }

    public Long getSubject(String token) {
        return Long.valueOf(getJwtParser().parseSignedClaims(token)
                .getPayload()
                .getSubject());
    }

    public Role getRole(String token) {
        return Role.valueOf(getJwtParser().parseSignedClaims(token)
                .getPayload()
                .get("role", String.class));
    }

    private String generateToken(Long memberId, Role role, long expireTime) {
        final Date now = new Date();
        final Date expiration = new Date(now.getTime() + expireTime);
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String decodeJwtPayloadSubject(String oldAccessToken) throws JsonProcessingException {
        return objectMapper.readValue(
                new String(Base64.getDecoder().decode(oldAccessToken.split("\\.")[1]), StandardCharsets.UTF_8),
                Map.class
        ).get("sub").toString();
    }
}
