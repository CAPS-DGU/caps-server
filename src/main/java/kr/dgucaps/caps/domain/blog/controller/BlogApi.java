package kr.dgucaps.caps.domain.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.dgucaps.caps.domain.blog.dto.request.CreateOrModifyBlogRequest;
import kr.dgucaps.caps.domain.blog.dto.response.BlogDetailResponse;
import kr.dgucaps.caps.domain.blog.dto.response.BlogListResponse;
import kr.dgucaps.caps.domain.blog.dto.response.BlogResponse;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Blog", description = "블로그 API")
public interface BlogApi {

    @Operation(
            summary = "게시물 목록 조회",
            description = "게시물 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlogListResponse.class)))
    })
    ResponseEntity<SuccessResponse<?>> getBlogs(
            @RequestParam(value = "category", required = false) BlogCategory category,
            @RequestParam(value = "page", required = false, defaultValue = "1") @Valid @Min(1) Integer page
    );

    @Operation(
            summary = "게시물 상세 조회",
            description = "게시물 상세 내용을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlogDetailResponse.class))),
            @ApiResponse(responseCode = "403", description = "비공개 처리된 게시물입니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물입니다.")
    })
    ResponseEntity<SuccessResponse<?>> getBlog(
            @PathVariable("blogId") Integer blogId
    );

    @Operation(
            summary = "게시물 작성",
            description = "새 블로그 게시물을 작성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시물 작성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlogResponse.class))),
            @ApiResponse(responseCode = "403", description = "게시물을 작성할 권한이 없습니다.")
    })
    ResponseEntity<SuccessResponse<?>> createBlog(
            @Auth Long memberId,
            @RequestBody @Valid CreateOrModifyBlogRequest request
    );

    @Operation(
            summary = "게시물 수정",
            description = "기존 블로그 게시물을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlogResponse.class))),
            @ApiResponse(responseCode = "403", description = "게시물을 수정할 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물입니다.")
    })
    ResponseEntity<SuccessResponse<?>> modifyBlog(
            @Auth Long memberId,
            @PathVariable("blogId") Integer blogId,
            @RequestBody @Valid CreateOrModifyBlogRequest request
    );

    @Operation(
            summary = "게시물 삭제",
            description = "블로그 게시물을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시물 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "게시물을 삭제할 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 게시물입니다.")
    })
    ResponseEntity<SuccessResponse<?>> deleteBlog(
            @PathVariable("blogId") Integer blogId
    );
}
