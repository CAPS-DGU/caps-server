package kr.dgucaps.caps.domain.ledger.dto.response;

import kr.dgucaps.caps.domain.dto.MemberSummary;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LedgerListResponse(
        Long id,
        String title,
        MemberSummary member,
        LocalDateTime createdAt,
        Boolean isPinned,
        Boolean hasFile
) {
    public static LedgerListResponse from(Ledger ledger) {
        return LedgerListResponse.builder()
                .id(ledger.getId())
                .title(ledger.getTitle())
                .member(MemberSummary.from(ledger.getMember()))
                .createdAt(ledger.getCreatedAt())
                .isPinned(ledger.isPinned())
                .hasFile(ledger.getFileUrls() != null && !ledger.getFileUrls().isEmpty())
                .build();
    }
}
