package kr.dgucaps.caps.domain.wiki.controller;

import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.wiki.dto.request.CreateOrModifyWikiRequest;
import kr.dgucaps.caps.domain.wiki.service.WikiService;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/wikis")
public class WikiController implements WikiApi {

    private final WikiService wikiService;

    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createWiki(@AuthenticationPrincipal Long memberId,
                                                         @Valid @RequestBody CreateOrModifyWikiRequest request) {
        return SuccessResponse.ok(wikiService.createWiki(memberId, request));
    }

    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @PatchMapping
    public ResponseEntity<SuccessResponse<?>> modifyWiki(@AuthenticationPrincipal Long memberId,
                                                         @Valid @RequestBody CreateOrModifyWikiRequest request) {
        return SuccessResponse.ok(wikiService.modifyWiki(memberId, request));
    }

    @GetMapping("/{title}")
    public ResponseEntity<SuccessResponse<?>> getWiki(@PathVariable("title") String title) {
        return SuccessResponse.ok(wikiService.getWiki(title));
    }

    @GetMapping("/{title}/history")
    public ResponseEntity<SuccessResponse<?>> getWikiHistory(@PathVariable(value = "title") String title) {
        return SuccessResponse.ok(wikiService.getWikiHistory(title));
    }

    @GetMapping("/random")
    public ResponseEntity<SuccessResponse<?>> getRandomWiki() {
        return SuccessResponse.ok(wikiService.getRandomWiki());
    }

    @GetMapping("/recent")
    public ResponseEntity<SuccessResponse<?>> getRecentWiki() {
        return SuccessResponse.ok(wikiService.getRecentWiki());
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<SuccessResponse<?>> getAutocompleteWiki(@RequestParam String input) {
        return SuccessResponse.ok(wikiService.getAutocompleteWiki(input));
    }

}