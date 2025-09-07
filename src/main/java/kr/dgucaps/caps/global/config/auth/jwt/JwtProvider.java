package kr.dgucaps.caps.global.config.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletResponse;
import kr.dgucaps.caps.domain.auth.dto.AuthDto;
import kr.dgucaps.caps.domain.auth.dto.CustomOAuth2User;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${app.auth.cookie.domain}")
    private String cookieDomain;

    @Value("${jwt.access-token-expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${jwt.consent-token-expire-time}")
    private long CONSENT_TOKEN_EXPIRE_TIME;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, ACCESS_TOKEN_EXPIRE_TIME, TokenType.ACCESS);
    }

    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, REFRESH_TOKEN_EXPIRE_TIME, TokenType.REFRESH);
    }

    public String generateConsentToken(Authentication authentication) {
        return generateToken(authentication, CONSENT_TOKEN_EXPIRE_TIME, TokenType.CONSENT);
    }

    public String generateToken(Authentication authentication, long expirationTime, TokenType tokenType) {
        String memberId = authentication.getName();
        JwtBuilder builder = Jwts.builder()
                .claim("memberId", Long.parseLong(memberId))
                .claim("purpose", tokenType.name());
        
        // ACCESS 토큰인 경우에만 Role 정보 추가
        if (tokenType == TokenType.ACCESS && authentication.getPrincipal() instanceof CustomOAuth2User customUser) {
            if (customUser.authDto().role() != null) {
                builder.claim("role", customUser.authDto().role().name());
            }
        }
        
        return builder
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String validateAccessToken(String token) {
        try {
            Claims claims = getJwtParser().parseSignedClaims(token).getPayload();
            return claims.get("purpose", String.class);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    public void validateRefreshToken(String token) {
        try {
            getJwtParser().parseSignedClaims(token);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (JwtException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getJwtParser().parseSignedClaims(token).getPayload();
        Long memberId = claims.get("memberId", Long.class);
        String roleString = claims.get("role", String.class);
        
        AuthDto.AuthDtoBuilder builder = AuthDto.builder().memberId(memberId);
        if (roleString != null) {
            builder.role(kr.dgucaps.caps.domain.member.entity.Role.valueOf(roleString));
        }
        
        AuthDto authDto = builder.build();
        CustomOAuth2User principal = new CustomOAuth2User(authDto);
        return new UsernamePasswordAuthenticationToken(principal, token, null);
    }

    public Authentication getAuthentication(String token, List<GrantedAuthority> authorities) {
        Claims claims = getJwtParser().parseSignedClaims(token).getPayload();
        Long memberId = claims.get("memberId", Long.class);
        String roleString = claims.get("role", String.class);
        
        AuthDto.AuthDtoBuilder builder = AuthDto.builder().memberId(memberId);
        if (roleString != null) {
            builder.role(kr.dgucaps.caps.domain.member.entity.Role.valueOf(roleString));
        }
        
        AuthDto authDto = builder.build();
        CustomOAuth2User principal = new CustomOAuth2User(authDto);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public void writeTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie.ResponseCookieBuilder accessBuilder = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(cookieDomain)
                .maxAge(StringUtils.hasText(accessToken) ? ACCESS_TOKEN_EXPIRE_TIME / 1000 : 0);

        ResponseCookie.ResponseCookieBuilder refreshBuilder = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(cookieDomain)
                .maxAge(StringUtils.hasText(refreshToken) ? REFRESH_TOKEN_EXPIRE_TIME / 1000 : 0);

        response.addHeader("Set-Cookie", accessBuilder.build().toString());
        response.addHeader("Set-Cookie", refreshBuilder.build().toString());
    }

    public void writeConsentTokenCookie(HttpServletResponse response, String consentToken) {
        writeTokenCookies(response, "", "");
        ResponseCookie.ResponseCookieBuilder consentBuilder = ResponseCookie.from("accessToken", consentToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .domain(cookieDomain)
                .maxAge(CONSENT_TOKEN_EXPIRE_TIME / 1000);

        response.addHeader("Set-Cookie", consentBuilder.build().toString());
    }

    private JwtParser getJwtParser() {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build();
    }
}