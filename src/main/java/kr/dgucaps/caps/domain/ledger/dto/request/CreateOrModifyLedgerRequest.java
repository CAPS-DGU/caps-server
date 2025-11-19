package kr.dgucaps.caps.domain.ledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import kr.dgucaps.caps.domain.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

public record CreateOrModifyLedgerRequest(
        @NotBlank String title,
        @NotBlank String content,
        String fileUrl,
        MultipartFile file

) {
    public Ledger toEntity(Member member, String resolvedFileUrl) {
         return Ledger.builder()
                .member(member)
                .title(title)
                .content(content)
                .fileUrl(resolvedFileUrl)
                .viewCount(0)
                .build();
    }

    public boolean hasNewFile() {
        return file != null && !file.isEmpty();
    }
}
