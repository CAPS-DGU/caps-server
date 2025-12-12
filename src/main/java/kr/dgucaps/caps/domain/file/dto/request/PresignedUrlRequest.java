package kr.dgucaps.caps.domain.file.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PresignedUrlRequest(
        @NotBlank(message = "파일명은 필수입니다.")
        String fileName,
        String fileType
) {
}

