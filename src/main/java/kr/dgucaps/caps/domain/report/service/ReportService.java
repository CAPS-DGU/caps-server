package kr.dgucaps.caps.domain.report.service;

import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.domain.report.dto.request.CreateReportRequest;
import kr.dgucaps.caps.domain.report.dto.response.ReportResponse;
import kr.dgucaps.caps.domain.report.entity.Report;
import kr.dgucaps.caps.domain.report.entity.ReportFile;
import kr.dgucaps.caps.domain.report.integration.ReportCreatedEvent;
import kr.dgucaps.caps.domain.report.repository.ReportFileRepository;
import kr.dgucaps.caps.domain.report.repository.ReportRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final ReportFileRepository reportFileRepository;
    private final ApplicationEventPublisher publisher;


    private List<ReportFile> getReportFiles(Report report, String[] fileUrls) {
        if (fileUrls == null) {
            return List.of();
        }
        return Arrays.stream(fileUrls)
                .filter(url -> url != null && !url.isBlank())
                .map(url -> new ReportFile(report, url))
                .toList();
    }

    @Transactional
    public ReportResponse createReport(Long memberId, CreateReportRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        Report report = reportRepository.save(request.toReportEntity(member));

        List<ReportFile> files = getReportFiles(report, request.fileUrls());
        if (!files.isEmpty()) {
            reportFileRepository.saveAll(files);
        }
        List<String> urls = files.stream()
                .map(ReportFile::getFileUrl)
                .toList();

        publisher.publishEvent(new ReportCreatedEvent(
                report,
                urls
        ));

        return ReportResponse.from(report, urls);
    }
}
