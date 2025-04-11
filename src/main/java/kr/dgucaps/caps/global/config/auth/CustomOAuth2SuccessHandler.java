package kr.dgucaps.caps.global.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.dgucaps.caps.domain.member.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.member.dto.response.MemberTokenResponse;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    @Value("${jwt.access-token-expire-time}")
    private int ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token-expire-time}")
    private int REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${app.auth.redirect.home}")
    private String homeRedirectUrl;

    @Value("${app.auth.redirect.onboarding}")
    private String onboardingRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Member member = customUser.member();

        // 토큰 발급
        MemberTokenResponse tokenResponse = tokenService.issueToken(member.getId(), member.getRole());
        response.addCookie(createCookie("accessToken", tokenResponse.accessToken(), ACCESS_TOKEN_EXPIRE_TIME));
        response.addCookie(createCookie("refreshToken", tokenResponse.refreshToken(), REFRESH_TOKEN_EXPIRE_TIME));

        // 추가 정보 입력 여부에 따라 리다이렉트
        if (member.isRegistrationComplete()) {
            response.sendRedirect(homeRedirectUrl);
        } else {
            response.sendRedirect(onboardingRedirectUrl);
        }
    }

    private Cookie createCookie(String key, String value, int expireTime) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expireTime);
        // 개발환경에서 주석(https 적용시 true)
//        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
