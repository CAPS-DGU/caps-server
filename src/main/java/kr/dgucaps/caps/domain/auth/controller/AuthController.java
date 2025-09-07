package kr.dgucaps.caps.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.auth.service.AuthService;
import kr.dgucaps.caps.domain.member.dto.request.CompleteRegistrationRequest;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;

    // 회원가입 후 추가 정보 입력
    @PatchMapping("/complete-registration")
    public ResponseEntity<SuccessResponse<?>> completeRegistration(@Auth Long memberId,
                                                                   @RequestBody @Valid CompleteRegistrationRequest request,
                                                                   HttpServletResponse response) {
        authService.completeRegistration(memberId, request, response);
        return SuccessResponse.noContent();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<?>> logout(@Auth Long memberId, HttpServletResponse response) {
        authService.logout(memberId, response);
        return SuccessResponse.noContent();
    }

    // 리프레쉬 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<?>> reissueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                           HttpServletResponse response) {
        authService.reissueToken(refreshToken, response);
        return SuccessResponse.noContent();
    }
}
