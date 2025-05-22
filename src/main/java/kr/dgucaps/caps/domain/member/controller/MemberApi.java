package kr.dgucaps.caps.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.member.dto.request.UpdateMemberRequest;
import kr.dgucaps.caps.domain.member.dto.response.MemberInfoResponse;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원 API")
public interface MemberApi {

    @Operation(
            summary = "내 정보 조회",
            description = "내 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "내 정보 조회 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> getMemberInfo(@AuthenticationPrincipal(expression = "member") Member member);

    @Operation(
            summary = "다른 회원 정보 조회",
            description = "본인이 아닌 회원을 번호로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다른 회원 정보 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MemberInfoResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원이 존재하지 않음")
    })
    ResponseEntity<SuccessResponse<?>> getOtherMemberInfo(@PathVariable("memberId") Long memberId);

    @Operation(
            summary = "내 정보 수정",
            description = "수정이 필요하지 않은 필드는 null로 요청하면 됩니다.  \n" +
                    "(body에 포함하지 않음)"
    )
    @ApiResponse(responseCode = "200", description = "내 정보 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MemberInfoResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> updateMember(@AuthenticationPrincipal(expression = "member") Member member,
                                                    @Valid @RequestBody UpdateMemberRequest request);
}
