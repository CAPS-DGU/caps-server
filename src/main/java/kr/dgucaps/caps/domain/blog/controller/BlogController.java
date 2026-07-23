package kr.dgucaps.caps.domain.blog.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.dgucaps.caps.domain.blog.dto.request.CreateOrModifyBlogRequest;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.service.BlogService;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blogs")
@Validated
public class BlogController implements BlogApi {

    private final BlogService blogService;

    // 게시물 목록 조회
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getBlogs(
            @RequestParam(value = "category", required = false) BlogCategory category,
            @RequestParam(value = "page", required = false, defaultValue = "1") @Valid @Min(1) Integer page
    ) {
        return SuccessResponse.ok(blogService.getBlogsByPage(category, page - 1));
    }

    // 게시물 상세 조회
    @GetMapping("/{blogId}")
    public ResponseEntity<SuccessResponse<?>> getBlog(
            @PathVariable("blogId") Integer blogId
    ) {
        return SuccessResponse.ok(blogService.getBlogById(blogId));
    }

    // 게시물 작성
    @PreAuthorize("hasAnyRole('COUNCIL', 'PRESIDENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createBlog(
            @Auth Long memberId,
            @RequestBody @Valid CreateOrModifyBlogRequest request
    ) {
        return SuccessResponse.created(blogService.createBlog(memberId, request));
    }

    // 게시물 수정
    @PreAuthorize("hasAnyRole('COUNCIL', 'PRESIDENT', 'ADMIN')")
    @PatchMapping("/{blogId}")
    public ResponseEntity<SuccessResponse<?>> modifyBlog(
            @Auth Long memberId,
            @PathVariable("blogId") Integer blogId,
            @RequestBody @Valid CreateOrModifyBlogRequest request
    ) {
        return SuccessResponse.ok(blogService.modifyBlog(blogId, memberId, request));
    }

    // 게시물 삭제
    @PreAuthorize("hasAnyRole('COUNCIL', 'PRESIDENT', 'ADMIN')")
    @DeleteMapping("/{blogId}")
    public ResponseEntity<Void> deleteBlog(
            @PathVariable("blogId") Integer blogId
    ) {
        blogService.deleteBlog(blogId);
        return SuccessResponse.noContent();
    }
}
