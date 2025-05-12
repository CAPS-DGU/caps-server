package kr.dgucaps.caps.domain.wiki.service;

import jakarta.annotation.PostConstruct;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.domain.redis.service.AutocompleteService;
import kr.dgucaps.caps.domain.wiki.dto.request.CreateOrModifyWikiRequest;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiResponse;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiTitleResponse;
import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import kr.dgucaps.caps.domain.wiki.repository.WikiRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WikiService {

    private final WikiRepository wikiRepository;
    private final MemberRepository memberRepository;
    private final AutocompleteService autocompleteService;

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

    private String suffix = "*";
    private int maxSize = 5;
    @PostConstruct
    public void init() {
        saveAllSubstring(wikiRepository.findAllTitle());
    }
    private void saveAllSubstring(List<String> allTitle) {
        for (String title : allTitle) {
            autocompleteService.addToSortedSet(title + suffix);
            for (int i = title.length(); i > 0; --i) {
                autocompleteService.addToSortedSet(title.substring(0, i));
            }
        }
    }
    public List<String> autocorrect(String keyword) {
        Long index = autocompleteService.findFromSortedSet(keyword);
        if (index == null) {
            return new ArrayList<>();
        }
        Set<String> allValuesAfterIndexFromSortedSet = autocompleteService.findAllValuesAfterIndexFromSortedSet(index);
        List<String> autocorrectKeywords = allValuesAfterIndexFromSortedSet.stream()
                .filter(value -> value.endsWith(suffix) && value.startsWith(keyword))
                .map(value -> StringUtils.removeEnd(value, suffix))
                .limit(maxSize)
                .toList();
        return autocorrectKeywords;
    }
}
