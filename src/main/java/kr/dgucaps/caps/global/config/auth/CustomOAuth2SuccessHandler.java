package kr.dgucaps.caps.global.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.domain.member.service.MemberService;
import kr.dgucaps.caps.domain.redis.entity.RefreshToken;
import kr.dgucaps.caps.domain.redis.repository.RefreshTokenRepository;
import kr.dgucaps.caps.global.config.auth.jwt.JwtProvider;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @Value("${app.auth.redirect.home}")
    private String homeRedirectUrl;

    @Value("${app.auth.redirect.onboarding}")
    private String onboardingRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Long memberId = Long.valueOf(authentication.getName());
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.MEMBER_NOT_FOUND));

        // 마지막 로그인 시간 업데이트
        memberService.updateLastLogin(member);

        // 추가 정보 입력 여부에 따라 리다이렉트
        if (!member.isRegistrationComplete()) {
            String consentToken = jwtProvider.generateConsentToken(authentication);
            jwtProvider.writeConsentTokenCookie(response, consentToken);
            response.sendRedirect(onboardingRedirectUrl);
            return;
        }

        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        RefreshToken refreshTokenEntity = new RefreshToken(authentication.getName(), refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        jwtProvider.writeTokenCookies(response, accessToken, refreshToken);
        response.sendRedirect(homeRedirectUrl);
    }
}
