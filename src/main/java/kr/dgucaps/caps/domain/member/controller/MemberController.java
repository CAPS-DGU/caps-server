package kr.dgucaps.caps.domain.member.controller;

import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.member.dto.request.UpdateMemberRequest;
import kr.dgucaps.caps.domain.member.service.MemberService;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import kr.dgucaps.caps.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController implements MemberApi {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<SuccessResponse<?>> getMemberInfo(@Auth Long memberId) {
        return SuccessResponse.ok(memberService.getMemberInfo(memberId));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<SuccessResponse<?>> getOtherMemberInfo(@Auth Long authMemberId,
                                                                 @PathVariable("memberId") Long memberId) {
        // 본인 정보만 조회 가능 (타인의 학번·전화번호·이메일 등 개인정보 노출 방지)
        if (!authMemberId.equals(memberId)) {
            throw new ForbiddenException();
        }
        return SuccessResponse.ok(memberService.getMemberInfo(memberId));
    }

    @PatchMapping("/me")
    public ResponseEntity<SuccessResponse<?>> updateMember(@Auth Long memberId,
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
