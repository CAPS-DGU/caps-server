package kr.dgucaps.caps.domain.report.dto.request;

import jakarta.validation.constraints.NotBlank;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.report.entity.Category;
import kr.dgucaps.caps.domain.report.entity.Report;

public record CreateReportRequest(
        @NotBlank String content,
        String[] fileUrls,
        Category category
) {
    public Report toReportEntity(Member member) {
        return Report.builder()
                .member(member)
                .content(content)
                .category(category)
                .build();
    }
}
