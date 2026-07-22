package kr.dgucaps.caps.domain.blog.integration;

import kr.dgucaps.caps.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlogS3FileDeleteListener {

    private final FileService fileService;

    // 트랜잭션 커밋 후 S3 파일 삭제
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBlogS3FilesDeleted(BlogS3FilesDeletedEvent event) {
        for (String fileKey : event.fileKeys()) {
            try {
                fileService.deleteFile(fileKey);
            } catch (Exception e) {
                log.warn("S3 파일 삭제 실패 after commit. fileKey={}", fileKey, e);
            }
        }
    }
}
