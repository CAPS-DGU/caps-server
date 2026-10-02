package kr.dgucaps.caps.domain.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.dgucaps.caps.domain.file.dto.request.PresignedUrlRequest;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "File", description = "파일 API")
public interface FileApi {

    @Operation(
            summary = "Presigned URL 발급 (업로드용)",
            description = "파일 업로드를 위한 Presigned URL을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "파일을 업로드할 권한이 없음")
    })
    ResponseEntity<SuccessResponse<?>> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    );

    @Operation(
            summary = "Presigned URL 발급 (다운로드용)",
            description = "장부 첨부파일 조회/다운로드를 위한 Presigned URL을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "파일을 조회할 권한이 없음")
    })
    ResponseEntity<SuccessResponse<?>> getPresignedDownloadUrl(
            @RequestParam("key") @NotBlank String fileKey,
            @Parameter(description = "첨부파일 다운로드 시 true. 생략하면 이미지 등은 inline으로 표시합니다.")
            @RequestParam(name = "download", defaultValue = "false") boolean download
    );

    @Operation(
            summary = "블로그 첨부파일 Presigned URL 발급",
            description = "블로그 첨부파일·본문 이미지·썸네일 조회/다운로드용 Presigned URL을 발급합니다. (비로그인 포함 공개)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "블로그에 등록되지 않은 파일")
    })
    ResponseEntity<SuccessResponse<?>> getBlogPresignedDownloadUrl(
            @RequestParam("key") @NotBlank String fileKey,
            @Parameter(description = "첨부파일 다운로드 시 true. 생략하면 이미지 등은 inline으로 표시합니다.")
            @RequestParam(name = "download", defaultValue = "false") boolean download
    );

    @Operation(
            summary = "파일 삭제",
            description = "S3에 저장된 파일을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "파일 삭제 성공 (본문 없음)"),
            @ApiResponse(responseCode = "401", description = "파일을 삭제할 권한이 없음")
    })
    ResponseEntity<Void> deleteFile(
            @RequestParam("key") @NotBlank String fileKey
    );

}
