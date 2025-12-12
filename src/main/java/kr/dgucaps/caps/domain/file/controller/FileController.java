package kr.dgucaps.caps.domain.file.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.dgucaps.caps.domain.file.dto.request.PresignedUrlRequest;
import kr.dgucaps.caps.domain.file.service.FileService;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/files")
@Validated
public class FileController {

    private final FileService fileService;

    // Presigned URL 발급 (파일 업로드용)
    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @PostMapping("/presigned-url")
    public ResponseEntity<SuccessResponse<?>> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request) {
        Map<String, String> result = fileService.generatePresignedUploadUrl(
                request.fileName(),
                request.fileType()
        );
        return SuccessResponse.ok(result);
    }

    // Presigned URL 발급 (다운로드용)
    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @GetMapping("/presigned-url")
    public ResponseEntity<SuccessResponse<?>> getPresignedDownloadUrl(
            @RequestParam("key") @NotBlank String fileKey) {
        String presignedUrl = fileService.generatePresignedDownloadUrl(fileKey);
        return SuccessResponse.ok(Map.of("downloadURL", presignedUrl));
    }

    // 파일 삭제
    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @DeleteMapping
    public ResponseEntity<SuccessResponse<?>> deleteFile(
            @RequestParam("key") @NotBlank String fileKey) {
        fileService.deleteFile(fileKey);
        return SuccessResponse.noContent();
    }
}

