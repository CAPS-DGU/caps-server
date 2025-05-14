package kr.dgucaps.caps.domain.wiki.dto.request;

import jakarta.validation.constraints.NotBlank;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.wiki.entity.Wiki;

public record CreateOrModifyWikiRequest(
        @NotBlank String title,
        @NotBlank String content
) {
    public Wiki toEntity(Member member) {
        return Wiki.builder()
                .member(member)
                .title(title)
                .content(content)
                .build();
    }
}
