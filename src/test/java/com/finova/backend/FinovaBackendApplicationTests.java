package com.finova.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.finova.backend.service.GeminiService;
import com.finova.backend.service.StatementS3Service;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/finova_test",
        "spring.datasource.username=test",
        "spring.datasource.password=test",
        "spring.data.redis.repositories.enabled=false",
        "spring.session.store-type=none",
        "spring.autoconfigure.exclude=org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration,org.springframework.boot.session.data.redis.autoconfigure.SessionDataRedisAutoConfiguration",
        "gemini.api-key=dummy-test-key",
        "AWS_S3_BUCKET=dummy-test-bucket"
})
class FinovaBackendApplicationTests {

    @MockitoBean
    private GeminiService geminiService;

    @MockitoBean
    private StatementS3Service statementS3Service;

    @Test
    void contextLoads() {
    }
}
