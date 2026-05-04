package kr.dgucaps.caps.domain.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import kr.dgucaps.caps.domain.auth.dto.AuthDto;
import kr.dgucaps.caps.domain.auth.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.member.dto.request.CompleteRegistrationRequest;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.entity.Role;
import kr.dgucaps.caps.domain.member.repository.MemberListRepository;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.domain.redis.entity.RefreshToken;
import kr.dgucaps.caps.domain.redis.repository.RefreshTokenRepository;
import kr.dgucaps.caps.global.config.auth.jwt.JwtProvider;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import kr.dgucaps.caps.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final MemberListRepository memberListRepository;

    public void reissueToken(String refreshToken, HttpServletResponse response) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        jwtProvider.validateRefreshToken(refreshToken);
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);

        // Redis에 저장된 refreshToken과 비교하여 일치하는지 확인
        String memberId = authentication.getName();
        RefreshToken refreshTokenEntity = refreshTokenRepository.findById(memberId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.NOT_MATCH_REFRESH_TOKEN));
        if (!refreshTokenEntity.getRefreshToken().equals(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.NOT_MATCH_REFRESH_TOKEN);
        }

        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        AuthDto authDto = AuthDto.of(member.getId(), member.getKakaoId(), member.getName(), member.getRole());
        Authentication refreshedAuthentication = new UsernamePasswordAuthenticationToken(
                new CustomOAuth2User(authDto), null, null);

        // 새 accessToken과 refreshToken 발급 (DB 최신 role 반영)
        String newAccessToken = jwtProvider.generateAccessToken(refreshedAuthentication);
        String newRefreshToken = jwtProvider.generateRefreshToken(refreshedAuthentication);

        // Redis에 새로 발급한 refreshToken 저장
        RefreshToken newRefreshTokenEntity = new RefreshToken(memberId, newRefreshToken);
        refreshTokenRepository.save(newRefreshTokenEntity);

        jwtProvider.writeTokenCookies(response, newAccessToken, newRefreshToken);
    }

    public void logout(Long memberId, HttpServletResponse response) {
        refreshTokenRepository.deleteById(memberId.toString());
        String accessToken = "";
        String refreshToken = "";
        jwtProvider.writeTokenCookies(response, accessToken, refreshToken);
    }

    @Transactional
    public void completeRegistration(Long memberId, CompleteRegistrationRequest request, HttpServletResponse response) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        member.completeRegistration(request.studentNumber(), request.grade(), request.phoneNumber());

        memberListRepository.findByStudentIdAndPhoneNumber(request.studentNumber(), request.phoneNumber().replaceAll("-", ""))
                .ifPresent(memberList -> member.updateRole(Role.MEMBER));

        AuthDto authDto = AuthDto.of(
                member.getId(), member.getKakaoId(), member.getName(), member.getRole());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new CustomOAuth2User(authDto), null, null);
        String accessToken = jwtProvider.generateAccessToken(authentication);
        String refreshToken = jwtProvider.generateRefreshToken(authentication);
        RefreshToken refreshTokenEntity = new RefreshToken(String.valueOf(member.getId()), refreshToken);
        refreshTokenRepository.save(refreshTokenEntity);
        jwtProvider.writeTokenCookies(response, accessToken, refreshToken);
    }
}
