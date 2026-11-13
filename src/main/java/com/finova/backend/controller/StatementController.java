package com.finova.backend.controller;

import com.finova.backend.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {

    private final StatementService statementService;

    // 거래명세서 생성 및 S3 업로드
    @PostMapping
    public ResponseEntity<Map<String, String>> create(
            Authentication authentication) throws IOException {

        String key = statementService.createStatement(
                authentication.getName());

        String statementId = key.substring(
                key.lastIndexOf('/') + 1,
                key.length() - 4);

        return ResponseEntity.ok(Map.of(
                "message", "거래명세서가 생성되었습니다.",
                "statementId", statementId
        ));
    }

    // 본인 거래명세서 PDF 다운로드
    @GetMapping("/{statementId}")
    public ResponseEntity<byte[]> download(
            @PathVariable String statementId,
            Authentication authentication) {

        byte[] pdf = statementService.downloadStatement(
                authentication.getName(),
                statementId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"finova-statement.pdf\"")
                .body(pdf);
    }
}
