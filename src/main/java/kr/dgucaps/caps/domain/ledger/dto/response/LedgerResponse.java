package kr.dgucaps.caps.domain.ledger.dto.response;

import kr.dgucaps.caps.domain.dto.MemberSummary;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record LedgerResponse(
        Long id,
        String title,
        String content,
        List<String> fileUrls,
        MemberSummary member,
        LocalDateTime createdAt,
        Boolean isPinned
) {
    public static LedgerResponse from(Ledger ledger) {
        return LedgerResponse.builder()
                .id(ledger.getId())
                .title(ledger.getTitle())
                .content(ledger.getContent())
                .fileUrls(ledger.getFileUrls() != null ? ledger.getFileUrls() : List.of())
                .member(MemberSummary.from(ledger.getMember()))
                .createdAt(ledger.getCreatedAt())
                .isPinned(ledger.isPinned())
                .build();
    }
}
