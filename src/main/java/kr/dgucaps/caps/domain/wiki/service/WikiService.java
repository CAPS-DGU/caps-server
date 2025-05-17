package kr.dgucaps.caps.domain.wiki.service;

import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.domain.wiki.dto.request.CreateOrModifyWikiRequest;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiResponse;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiTitleResponse;
import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import kr.dgucaps.caps.domain.wiki.repository.WikiRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WikiService {

    private final WikiRepository wikiRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public WikiResponse createOrModifyWiki(Long memberId, CreateOrModifyWikiRequest request) {
        if (wikiRepository.existsByTitleAndIsDeletedFalse(request.title())) {
            wikiRepository.deleteByTitleAndIsDeletedFalse(request.title());
        }
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Wiki savedWiki = wikiRepository.save(request.toEntity(member));
        return WikiResponse.from(savedWiki);
    }

    public WikiResponse getWiki(String title) {
        Wiki wiki = wikiRepository.findByTitleAndIsDeletedFalse(title)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WIKI_NOT_FOUND));
        return WikiResponse.from(wiki);
    }

    public List<WikiResponse> getWikiHistory(String title) {
        List<Wiki> wikiHistory = wikiRepository.findByTitleOrderByCreatedAtDesc(title);
        return wikiHistory.stream()
                .map(WikiResponse::from)
                .collect(Collectors.toList());
    }

    public WikiResponse getRandomWiki() {
        Wiki randomWiki = wikiRepository.findRandomWiki()
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WIKI_NOT_FOUND));
        return WikiResponse.from(randomWiki);
    }

    public List<WikiTitleResponse> getRecentWiki() {
        List<Wiki> recentWikis = wikiRepository.findFirst10ByIsDeletedFalseOrderByCreatedAtDesc();
        return recentWikis.stream()
                .map(WikiTitleResponse::from)
                .collect(Collectors.toList());
    }

    public List<WikiTitleResponse> getAutocompleteWiki(String input) {
        List<Wiki> autocompleteWikis = wikiRepository.findByJamoStartsWith(input);
        return autocompleteWikis.stream()
                .map(WikiTitleResponse::from)
                .collect(Collectors.toList());
    }

}
