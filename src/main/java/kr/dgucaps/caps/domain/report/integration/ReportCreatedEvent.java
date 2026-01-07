package kr.dgucaps.caps.domain.report.integration;

import kr.dgucaps.caps.domain.report.entity.Report;

import java.util.List;

public record ReportCreatedEvent(
        Report report,
        List<String> fileUrls
) {}
