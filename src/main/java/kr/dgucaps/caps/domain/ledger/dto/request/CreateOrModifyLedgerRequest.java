package kr.dgucaps.caps.domain.ledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import kr.dgucaps.caps.domain.member.entity.Member;

public record CreateOrModifyLedgerRequest(
        @NotBlank String title,
        @NotBlank String content,
        String fileUrl
) {
    public Ledger toEntity(Member member) {
        return Ledger.builder()
                .member(member)
                .title(title)
                .content(content)
                .fileUrl(fileUrl)
                .viewCount(0)
                .build();
    }
}
