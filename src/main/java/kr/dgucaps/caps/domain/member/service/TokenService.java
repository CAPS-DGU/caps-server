package kr.dgucaps.caps.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.dgucaps.caps.domain.member.dto.request.MemberTokenRequest;
import kr.dgucaps.caps.domain.member.dto.response.MemberTokenResponse;
import kr.dgucaps.caps.domain.member.entity.Role;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.domain.redis.entity.RefreshToken;
import kr.dgucaps.caps.domain.redis.repository.RefreshTokenRepository;
import kr.dgucaps.caps.global.config.auth.jwt.JwtProvider;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import kr.dgucaps.caps.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static kr.dgucaps.caps.global.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public String issueNewAccessToken(Long memberId) {
        Role role = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND))
                .getRole();
        return jwtProvider.getIssueToken(memberId, role, true);
    }

    public String issueNewRefreshToken(Long memberId) {
        Role role = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(MEMBER_NOT_FOUND))
                .getRole();
        return jwtProvider.getIssueToken(memberId, role, false);
    }

    public MemberTokenResponse issueToken(Long memberId, Role role) {
        String accessToken = jwtProvider.getIssueToken(memberId, role, true);
        String redisKey = "RT:" + memberId;
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(redisKey);
        RefreshToken refreshTokenEntity;
        if (refreshToken.isPresent()) {
            refreshTokenEntity = refreshToken.get();
        } else {
            String newRefreshToken = issueNewRefreshToken(memberId);
            refreshTokenEntity = new RefreshToken(redisKey, newRefreshToken);
            refreshTokenRepository.save(refreshTokenEntity);
        }
        return MemberTokenResponse.of(accessToken, refreshTokenEntity.getRefreshToken());
    }

    public MemberTokenResponse reissue(MemberTokenRequest request) {
        Long memberId;
        try {
            memberId = Long.valueOf(jwtProvider.decodeJwtPayloadSubject(request.accessToken()));
        } catch (JsonProcessingException e) {
            throw new UnauthorizedException(JSON_PARSING_FAILED);
        }
        String refreshToken = request.refreshToken();
        String redisKey = String.valueOf(memberId);

        // 리프레시 토큰 검증 (리프레시 토큰 만료시 재로그인 필요)
        jwtProvider.validateRefreshToken(refreshToken);

        // 저장된 refresh token을 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findById(redisKey)
                .orElseThrow(() -> new UnauthorizedException(NO_REFRESH_TOKEN));
        String storedRefreshToken = refreshTokenEntity.getRefreshToken();

        // 요청된 리프레시 토큰과 저장된 토큰 비교 검증
        jwtProvider.equalsRefreshToken(refreshToken, storedRefreshToken);

        // 새로운 토큰 발급
        String newAccessToken = issueNewAccessToken(memberId);
        String newRefreshToken = issueNewRefreshToken(memberId);

        // 새로운 refresh token을 Redis에 업데이트 (같은 키로 저장하면 기존 값이 덮어씌워짐)
        RefreshToken updatedToken = new RefreshToken(redisKey, newRefreshToken);
        refreshTokenRepository.save(updatedToken);

        return MemberTokenResponse.of(newAccessToken, newRefreshToken);
    }

    public void logout(Long memberId) {
        String redisKey = "RT:" + memberId;
        refreshTokenRepository.deleteById(redisKey);
    }
}
