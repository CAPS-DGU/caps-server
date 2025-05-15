package kr.dgucaps.caps.global.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.dgucaps.caps.domain.member.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.member.dto.response.MemberTokenResponse;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.service.MemberService;
import kr.dgucaps.caps.domain.member.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final MemberService memberService;

    @Value("${jwt.access-token-expire-time}")
    private int ACCESS_TOKEN_EXPIRE_TIME;

    @Value("${jwt.refresh-token-expire-time}")
    private int REFRESH_TOKEN_EXPIRE_TIME;

    @Value("${app.auth.redirect.home}")
    private String homeRedirectUrl;

    @Value("${app.auth.redirect.onboarding}")
    private String onboardingRedirectUrl;

    @Value("${app.auth.cookie.domain}")
    private String cookieDomain;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Member member = customUser.member();

        // 토큰 발급
        MemberTokenResponse tokenResponse = tokenService.issueToken(member.getId(), member.getRole());
        writeTokenCookies(response, tokenResponse);

        // 마지막 로그인 시간 업데이트
        memberService.updateLastLogin(member);

        // 추가 정보 입력 여부에 따라 리다이렉트
        if (member.isRegistrationComplete()) {
            response.sendRedirect(homeRedirectUrl);
        } else {
            response.sendRedirect(onboardingRedirectUrl);
        }
    }

    private void writeTokenCookies(HttpServletResponse response, MemberTokenResponse token) {
        // 쿠키 설정
        ResponseCookie accessToken = ResponseCookie.from("accessToken", token.accessToken())
                .path("/")
                .httpOnly(true)
                .secure(true)                  // HTTPS 연결에서만 전송
                .sameSite("Lax")              // 크로스사이트 요청에도 전송
                .maxAge(ACCESS_TOKEN_EXPIRE_TIME)
                .domain(cookieDomain)
                .build();

        ResponseCookie refreshToken = ResponseCookie.from("refreshToken", token.refreshToken())
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .maxAge(REFRESH_TOKEN_EXPIRE_TIME)
                .domain(cookieDomain)
                .build();

        // 쿠키를 응답 헤더에 추가
        response.addHeader("Set-Cookie", accessToken.toString());
        response.addHeader("Set-Cookie", refreshToken.toString());
    }
}
