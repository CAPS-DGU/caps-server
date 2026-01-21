package kr.dgucaps.caps.domain.report.dto.response;

import kr.dgucaps.caps.domain.report.entity.Category;
import kr.dgucaps.caps.domain.report.entity.Report;
import lombok.Builder;

import java.util.List;

@Builder
public record ReportResponse(
        Integer id,
        String content,
        Category category,
        List<String> fileUrls
) {
    public static ReportResponse from(Report report, List<String> fileUrls) {
        return ReportResponse.builder()
                .id(report.getId())
                .content(report.getContent())
                .category(report.getCategory())
                .fileUrls(fileUrls)
                .build();
    }
}
