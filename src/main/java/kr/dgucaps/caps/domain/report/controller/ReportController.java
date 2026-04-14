package kr.dgucaps.caps.domain.report.controller;

import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.report.dto.request.CreateReportRequest;
import kr.dgucaps.caps.domain.report.service.ReportService;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController implements ReportApi {

    private final ReportService reportService;

    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @Override
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createReport(
            @Auth Long memberId,
            @RequestBody @Valid CreateReportRequest request
    ) {
        return SuccessResponse.ok(reportService.createReport(memberId, request));
    }
}
