package kr.dgucaps.caps.domain.blog.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.service.BlogService;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blogs")
@Validated
public class BlogController implements BlogApi {

    private final BlogService blogService;

    // 카테고리별 게시물 목록을 조회한다.
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getBlogs(
            @RequestParam("category") @NotNull BlogCategory category,
            @RequestParam(value = "page", required = false, defaultValue = "1") @Valid @Min(1) Integer page,
            @Auth Long memberId
    ) {
        return SuccessResponse.ok(blogService.getBlogsByPage(category, page - 1, memberId));
    }
}
