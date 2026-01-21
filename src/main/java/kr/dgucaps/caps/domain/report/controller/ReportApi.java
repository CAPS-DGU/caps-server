package kr.dgucaps.caps.domain.report.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.report.dto.request.CreateReportRequest;
import kr.dgucaps.caps.domain.report.dto.response.ReportResponse;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Report", description = "문의 신고 API")
public interface ReportApi {
    @Operation(
            summary = "문의 및 신고 작성",
            description = "문의 및 신고 내용을 서버에 제출합니다"
    )
    @ApiResponse(responseCode = "200", description = "문의 및 신고 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ReportResponse.class))
    )
    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    ResponseEntity<SuccessResponse<?>> createReport(
            @Auth Long memberId,
            @RequestBody @Valid CreateReportRequest request
    );
}
