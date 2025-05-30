package kr.dgucaps.caps.domain.member.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.member.dto.request.CompleteRegistrationRequest;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.service.AuthService;
import kr.dgucaps.caps.domain.member.service.TokenService;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final TokenService tokenService;

    // 회원가입 후 추가 정보 입력
    @PatchMapping("/complete-registration")
    public ResponseEntity<SuccessResponse<?>> completeRegistration(@AuthenticationPrincipal(expression = "member") Member member,
                                                                   @RequestBody @Valid CompleteRegistrationRequest request) {
        return SuccessResponse.ok(authService.completeRegistration(member, request));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<?>> logout(@AuthenticationPrincipal(expression = "member") Member member) {
        tokenService.logout(member);
        return SuccessResponse.noContent();
    }

    // 리프레쉬 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<?>> reissueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                           HttpServletResponse response) {
        tokenService.reissue(refreshToken, response);
        return SuccessResponse.noContent();
    }

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<?>> getAuthInfo(@AuthenticationPrincipal(expression = "member") Member member) {
        return SuccessResponse.ok(authService.getAuthInfo(member));
    }
}
