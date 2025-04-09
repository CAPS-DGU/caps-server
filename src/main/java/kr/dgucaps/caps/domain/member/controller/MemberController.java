package kr.dgucaps.caps.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.member.dto.request.UpdateMemberRequest;
import kr.dgucaps.caps.domain.member.service.MemberService;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회")
    public ResponseEntity<SuccessResponse<?>> getMemberInfo(@AuthenticationPrincipal Long memberId) {
        return SuccessResponse.ok(memberService.getMemberInfo(memberId));
    }

    @GetMapping("/{memberId}")
    @Operation(summary = "번호로 회원 조회", description = "회원 정보 조회")
    public ResponseEntity<SuccessResponse<?>> getOtherMemberInfo(@PathVariable("memberId") Long memberId) {
        return SuccessResponse.ok(memberService.getMemberInfo(memberId));
    }

    @PatchMapping("/me")
    @Operation(summary = "회원 정보 수정", description = "수정이 필요하지 않은 필드는 null로 요청")
    public ResponseEntity<SuccessResponse<?>> updateMember(@AuthenticationPrincipal Long memberId,
                                                           @Valid @RequestBody UpdateMemberRequest request) {
        return SuccessResponse.ok(memberService.updateMember(memberId, request));
    }

//    @DeleteMapping("/user/{userId}")
//    @Operation(summary = "회원 탈퇴")
//    public ResponseEntity<DataResponse> deleteUser(@PathVariable("userId") String userId) {
//        userService.deleteUser(userId);
//        return ResponseEntity.ok(DataResponse.builder().message("회원 탈퇴 성공").build());
//    }
}
