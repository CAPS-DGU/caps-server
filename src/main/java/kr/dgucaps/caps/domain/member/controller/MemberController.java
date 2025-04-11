package kr.dgucaps.caps.domain.member.controller;

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
public class MemberController implements MemberApi {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<?>> getMemberInfo(@AuthenticationPrincipal Long memberId) {
        return SuccessResponse.ok(memberService.getMemberInfo(memberId));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<?>> getOtherMemberInfo(@PathVariable("memberId") Long memberId) {
        return SuccessResponse.ok(memberService.getMemberInfo(memberId));
    }

    @PatchMapping("/me")
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
