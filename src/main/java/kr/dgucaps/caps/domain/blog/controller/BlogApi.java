package kr.dgucaps.caps.domain.blog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import kr.dgucaps.caps.domain.blog.dto.response.BlogListResponse;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Blog", description = "블로그 API")
public interface BlogApi {

    @Operation(
            summary = "게시물 목록 조회",
            description = "카테고리별 게시물 목록을 조회합니다. 비공개 게시물은 작성자/PRESIDENT/ADMIN에게만 노출됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BlogListResponse.class)))
    })
    ResponseEntity<SuccessResponse<?>> getBlogs(
            @RequestParam("category") @NotNull BlogCategory category,
            @RequestParam(value = "page", required = false, defaultValue = "1") @Valid @Min(1) Integer page,
            @Auth Long memberId
    );
}
