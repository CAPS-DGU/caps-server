package kr.dgucaps.caps.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.member.dto.request.CompleteRegistrationRequest;
import kr.dgucaps.caps.domain.member.dto.response.AuthInfoResponse;
import kr.dgucaps.caps.domain.member.dto.response.MemberInfoResponse;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

    @Operation(
            summary = "회원가입 후 추가 정보 입력",
            description = "회원가입 후 추가 정보를 입력합니다."
    )
    @ApiResponse(responseCode = "200", description = "회원가입 후 추가 정보 입력 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> completeRegistration(@AuthenticationPrincipal Long memberId,
                                                                   @RequestBody @Valid CompleteRegistrationRequest request);

    @Operation(
            summary = "로그아웃",
            description = "로그아웃합니다.  \n" +
                    "서버가 저장하고 있는 리프레쉬 토큰을 무효화합니다.  \n" +
                    "클라이언트의 액세스, 리프레쉬 토큰을 삭제해야합니다."
    )
    @ApiResponse(responseCode = "204", description = "로그아웃 성공")
    ResponseEntity<SuccessResponse<?>> logout(@AuthenticationPrincipal Long userId);

    @Operation(
            summary = "토큰 재발급",
            description = "리프레쉬 토큰을 이용하여 토큰을 재발급합니다.  \n" +
                    "리프레쉬 토큰이 만료되었거나 유효하지 않은 경우 401 에러가 발생합니다.  \n" +
                    "재발급한 토큰은 쿠키로 저장됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
        @ApiResponse(responseCode = "401", description = "리프레쉬 토큰이 유효하지 않음"),
        @ApiResponse(responseCode = "500", description = "리프레쉬 토큰이 파싱 실패"),
    })
    ResponseEntity<SuccessResponse<?>> reissueToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                           HttpServletResponse response);

    @Operation(
            summary = "인증 정보 조회",
            description = "현재 로그인한 사용자의 인증 정보 및 온보딩 완료 여부를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthInfoResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 정보가 없음")
    })
    ResponseEntity<SuccessResponse<?>> getAuthInfo(@AuthenticationPrincipal Long memberId);
}
