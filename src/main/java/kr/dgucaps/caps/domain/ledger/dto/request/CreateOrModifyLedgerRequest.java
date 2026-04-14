package kr.dgucaps.caps.domain.ledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import kr.dgucaps.caps.domain.member.entity.Member;

import java.util.List;

public record CreateOrModifyLedgerRequest(
        @NotBlank String title,
        @NotBlank String content,
        List<String> fileUrls,
        Boolean isPinned
) {
    public Ledger toEntity(Member member) {
        return Ledger.builder()
                .member(member)
                .title(title)
                .content(content)
                .fileUrls(fileUrls)
                .isPinned(isPinned)
                .build();
    }
}
