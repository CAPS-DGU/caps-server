package kr.dgucaps.caps.domain.blog.integration;

import java.util.List;

public record BlogS3FilesDeletedEvent(
        List<String> fileKeys
) {
}
