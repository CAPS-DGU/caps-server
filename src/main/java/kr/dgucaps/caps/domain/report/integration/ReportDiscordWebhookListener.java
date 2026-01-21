package kr.dgucaps.caps.domain.report.integration;

import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportDiscordWebhookListener {

    private final DiscordWebhookSender discordWebhookSender;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReportCreated(ReportCreatedEvent event) {
        try {
            String files = (event.fileUrls() == null || event.fileUrls().isEmpty())
                    ? "(없음)"
                    : String.join("\n", event.fileUrls());

            String msg = getString(event, files);
            discordWebhookSender.send(msg);
        } catch (Exception e) {
            log.warn("Discord webhook failed after commit. reportId={}", event.report().getId(), e);
        }
    }

    private static @NonNull String getString(ReportCreatedEvent event, String files) {
        Report report = event.report();
        Member member = report.getMember();

        return """
            ❤️ **문의/신고 등록** ❤️
            
            문의 번호: #%s
            문의 유형: %s
            
            문의자: %s기 %s
            문의 내용: %s
            
            파일
            %s
            
            **✅ 해결 시 체크 표시를 남겨주세요!**
                """.formatted(
                report.getId(),
                report.getCategory().getTitle(),
                member.getGrade(),
                member.getName(),
                report.getContent(),
                files
        );
    }
}
