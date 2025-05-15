package kr.dgucaps.caps.domain.wiki.dto.response;

import kr.dgucaps.caps.domain.dto.MemberSummary;
import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import kr.dgucaps.caps.domain.wiki.util.WikiJamoUtils;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WikiResponse(
        String title,
        String content,
        MemberSummary editor,
        LocalDateTime createdAt,
        String jamo
) {
    public static WikiResponse from(Wiki wiki) {
        return WikiResponse.builder()
                .title(wiki.getTitle())
                .content(wiki.getContent())
                .editor(MemberSummary.from(wiki.getEditor()))
                .createdAt(wiki.getCreatedAt())
                .jamo(WikiJamoUtils.convertToJamo(wiki.getTitle()))
                .build();
    }
}
