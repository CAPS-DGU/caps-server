package kr.dgucaps.caps.domain.wiki.dto.response;

import kr.dgucaps.caps.domain.dto.MemberSummary;
import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import kr.dgucaps.caps.domain.wiki.entity.WikiHistory;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WikiResponse(
        String title,
        String content,
        MemberSummary member,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static WikiResponse from(Wiki wiki) {
        return WikiResponse.builder()
                .title(wiki.getTitle())
                .content(wiki.getContent())
                .member(MemberSummary.from(wiki.getMember()))
                .createdAt(wiki.getCreatedAt())
                .updatedAt(wiki.getUpdatedAt())
                .build();
    }

    public static WikiResponse from(WikiHistory wikiHistory) {
        return WikiResponse.builder()
                .title(wikiHistory.getTitle())
                .content(wikiHistory.getContent())
                .member(MemberSummary.from(wikiHistory.getMember()))
                .createdAt(wikiHistory.getCreatedAt())
                .updatedAt(wikiHistory.getUpdatedAt())
                .build();
    }
}
