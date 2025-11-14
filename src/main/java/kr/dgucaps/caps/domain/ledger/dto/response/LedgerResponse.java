package kr.dgucaps.caps.domain.ledger.dto.response;

import kr.dgucaps.caps.domain.dto.MemberSummary;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LedgerResponse(
        Long id,
        String title,
        String content,
        String fileUrl,
        MemberSummary member,
        Integer viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static LedgerResponse from(Ledger ledger) {
        return LedgerResponse.builder()
                .id(ledger.getId())
                .title(ledger.getTitle())
                .content(ledger.getContent())
                .fileUrl(ledger.getFileUrl())
                .member(MemberSummary.from(ledger.getMember()))
                .createdAt(ledger.getCreatedAt())
                .updatedAt(ledger.getUpdatedAt())
                .viewCount(ledger.getViewCount())
                .build();
    }
}
