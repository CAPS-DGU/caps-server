package kr.dgucaps.caps.domain.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * Presigned URL 발급 (업로드용)
     * @param fileName 파일명 (경로 포함 가능)
     * @param fileType 파일 MIME 타입
     * @return Presigned URL과 파일명을 포함한 Map
     */
    public Map<String, String> generatePresignedUploadUrl(String fileName, String fileType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(fileType != null ? fileType : "application/octet-stream")
                    .build();

            try (S3Presigner presigner = S3Presigner.create()) {
                PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5)) // 5분 유효
                        .putObjectRequest(putObjectRequest)
                        .build();

                PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
                String presignedUrl = presignedRequest.url().toString();

                log.info("Presigned URL 생성 완료: fileName={}, fileType={}", fileName, fileType);
                return Map.of(
                        "uploadURL", presignedUrl,
                        "fileName", fileName
                );
            }
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: fileName={}, fileType={}", fileName, fileType, e);
            throw new RuntimeException("Presigned URL 생성에 실패했습니다.", e);
        }
    }

    /**
     * Presigned URL 발급 (다운로드용)
     * @param fileKey 파일 키 (경로)
     * @return Presigned URL
     */
    public String generatePresignedDownloadUrl(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            try (S3Presigner presigner = S3Presigner.create()) {
                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15)) // 15분 유효
                        .getObjectRequest(getObjectRequest)
                        .build();

                PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
                String presignedUrl = presignedRequest.url().toString();

                log.info("Presigned URL 생성 완료 (다운로드): fileKey={}", fileKey);
                return presignedUrl;
            }
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패 (다운로드): fileKey={}", fileKey, e);
            throw new RuntimeException("Presigned URL 생성에 실패했습니다.", e);
        }
    }

    /**
     * S3에서 파일 삭제
     * @param fileKey 삭제할 파일의 키 (경로)
     */
    public void deleteFile(String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("파일 삭제 완료: fileKey={}", fileKey);
        } catch (Exception e) {
            log.error("파일 삭제 실패: fileKey={}", fileKey, e);
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }
}

