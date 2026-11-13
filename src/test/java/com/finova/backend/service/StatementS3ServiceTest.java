package com.finova.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StatementS3ServiceTest {

    private S3Client s3Client;
    private StatementS3Service service;

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        service = new StatementS3Service(s3Client);

        ReflectionTestUtils.setField(
                service,
                "bucket",
                "finova-test-bucket"
        );
    }

    @Test
    void uploadUsesUserScopedKey() {

        byte[] pdf = "%PDF-test".getBytes();

        String key = service.uploadPdf(1L, pdf);

        assertTrue(key.matches(
                "statements/1/[0-9a-f-]{36}\\.pdf"
        ));

        verify(s3Client).putObject(
                argThat((PutObjectRequest req) ->
                        req.bucket().equals("finova-test-bucket")
                        && req.key().equals(key)
                        && req.contentType().equals("application/pdf")),
                any(RequestBody.class)
        );
    }

    @Test
    void downloadReturnsPdfBytes() {

        String key = "statements/1/test.pdf";
        byte[] pdf = "%PDF-test".getBytes();

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
                .thenReturn(ResponseBytes.fromByteArray(
                        GetObjectResponse.builder().build(),
                        pdf
                ));

        byte[] result = service.downloadPdf(key);

        assertArrayEquals(pdf, result);

        verify(s3Client).getObjectAsBytes(
                argThat((GetObjectRequest req) ->
                        req.bucket().equals("finova-test-bucket")
                        && req.key().equals(key))
        );
    }
}
