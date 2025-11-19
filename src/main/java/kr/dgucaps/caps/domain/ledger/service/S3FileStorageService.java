package kr.dgucaps.caps.domain.ledger.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import kr.dgucaps.caps.global.error.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class S3FileStorageService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.keyFormat}")
    private String keyFormat;

    public S3FileStorageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(String.valueOf(ErrorCode.BAD_REQUEST));
        }
        try {
            String originalFileName = file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String key = String.format(keyFormat, uuid, originalFileName);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(
                    new PutObjectRequest(bucketName, key, file.getInputStream(), metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead)
            );

            return amazonS3.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            throw new RuntimeException(String.valueOf(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String decoded = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);

            return decoded.startsWith("//") ? decoded.substring(2) : decoded;

        } catch (Exception e) {
            throw new RuntimeException("Invalid S3 URL: " + fileUrl);
        }
    }

    public void remove(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            amazonS3.deleteObject(bucketName, key);
            System.out.println("Removed S3 URL: " + key);
        } catch (AmazonS3Exception e) {
            throw new RuntimeException(String.valueOf(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
}
