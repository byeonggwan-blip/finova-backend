package com.finova.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementS3Service {

    private final S3Client s3Client;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    public String uploadPdf(Long userId, byte[] pdf) {

        if (userId == null || pdf == null || pdf.length == 0) {
            throw new IllegalArgumentException(
                    "사용자 ID 또는 PDF 데이터가 올바르지 않습니다.");
        }

        String key = "statements/"
                + userId + "/"
                + UUID.randomUUID() + ".pdf";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("application/pdf")
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromBytes(pdf));

        return key;
    }

    public byte[] downloadPdf(String key) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try {
            return s3Client.getObjectAsBytes(request)
                    .asByteArray();
        } catch (software.amazon.awssdk.services.s3.model.NoSuchKeyException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "명세서를 찾을 수 없습니다.");
        }
    }
}
