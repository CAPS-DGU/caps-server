package kr.dgucaps.caps.domain.wiki.dto.response;

import kr.dgucaps.caps.domain.wiki.entity.Wiki;

public record WikiTitleResponse(
        String title
) {
    public static WikiTitleResponse from(Wiki wiki) {
        return new WikiTitleResponse(wiki.getTitle());
    }
}
