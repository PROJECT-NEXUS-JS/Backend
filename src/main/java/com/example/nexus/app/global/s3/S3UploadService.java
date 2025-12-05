package com.example.nexus.app.global.s3;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty() || multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().isBlank()) {
            throw new GeneralException(ErrorStatus.FILE_IS_EMPTY);
        }
        String originalFilename = multipartFile.getOriginalFilename();
        String uniqueFilename = createUniqueFilename(originalFilename);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFilename)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));
            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(uniqueFilename)).toExternalForm();

        } catch (IOException e) {
            log.error("S3 파일 업로드 중 IO 에러 발생: filename={}, error={}", originalFilename, e.getMessage(), e);
            throw new GeneralException(ErrorStatus.S3_UPLOAD_FAILED);
        } catch (SdkException e) {
            log.error("S3 SDK 에러 발생: filename={}, bucket={}, error={}", originalFilename, bucketName, e.getMessage(), e);
            throw new GeneralException(ErrorStatus.S3_UPLOAD_FAILED);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.S3_DELETE_FAILED);
        }
    }

    private String createUniqueFilename(String originalFilename) {
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex);
        }
        return UUID.randomUUID() + extension;
    }

    private String extractKeyFromUrl(String fileUrl) {
        // filname.jpg 추출
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }
}
