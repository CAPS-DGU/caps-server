package kr.dgucaps.caps.domain.wiki.service;

import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.domain.wiki.dto.request.CreateOrModifyWikiRequest;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiResponse;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiTitleResponse;
import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import kr.dgucaps.caps.domain.wiki.entity.WikiHistory;
import kr.dgucaps.caps.domain.wiki.repository.WikiHistoryRepository;
import kr.dgucaps.caps.domain.wiki.repository.WikiRepository;
import kr.dgucaps.caps.domain.wiki.util.WikiJamoUtils;
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
    private final WikiHistoryRepository wikiHistoryRepository;

    @Transactional
    public WikiResponse createWiki(Long memberId, CreateOrModifyWikiRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Wiki savedWiki = wikiRepository.save(request.toEntity(member));
        return WikiResponse.from(savedWiki);
    }

    @Transactional
    public WikiResponse modifyWiki(Long memberId, CreateOrModifyWikiRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Wiki wiki = wikiRepository.findByTitle(request.title())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WIKI_NOT_FOUND));
        WikiHistory wikiHistory = WikiHistory.builder()
                .wiki(wiki)
                .member(wiki.getMember())
                .title(wiki.getTitle())
                .content(wiki.getContent())
                .build();
        wikiHistoryRepository.save(wikiHistory);
        wiki.updateWiki(member, request.title(), request.content());
        Wiki updatedWiki = wikiRepository.save(wiki);
        return WikiResponse.from(updatedWiki);
    }

    public WikiResponse getWiki(String title) {
        Wiki wiki = wikiRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WIKI_NOT_FOUND));
        return WikiResponse.from(wiki);
    }

    public List<WikiResponse> getWikiHistory(String title) {
        List<WikiHistory> wikiHistories = wikiHistoryRepository.findByTitleOrderByCreatedAtDesc(title);
        return wikiHistories.stream()
                .map(WikiResponse::from)
                .collect(Collectors.toList());
    }

    public WikiResponse getRandomWiki() {
        Wiki randomWiki = wikiRepository.findRandomWiki()
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.WIKI_NOT_FOUND));
        return WikiResponse.from(randomWiki);
    }

    public List<WikiTitleResponse> getRecentWiki() {
        List<Wiki> recentWikis = wikiRepository.findFirst10ByOrderByUpdatedAtDesc();
        return recentWikis.stream()
                .map(WikiTitleResponse::from)
                .collect(Collectors.toList());
    }

    public List<WikiTitleResponse> getAutocompleteWiki(String input) {
        List<Wiki> autocompleteWikis = wikiRepository.findByJamoStartsWith(WikiJamoUtils.convertToJamo(input));
        return autocompleteWikis.stream()
                .map(WikiTitleResponse::from)
                .collect(Collectors.toList());
    }

    // 배포 후 삭제
    @Transactional
    public void updateExistingDataJamo() {
        List<Wiki> wikis = wikiRepository.findAll();
        for (Wiki wiki : wikis) {
            String jamo = WikiJamoUtils.convertToJamo(wiki.getTitle());
            wiki.updateJamo(jamo);
            wikiRepository.save(wiki);
        }
    }
}
