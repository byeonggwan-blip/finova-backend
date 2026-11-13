package com.finova.backend.service;

import com.finova.backend.dto.TransactionDto;
import com.finova.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final UserMapper userMapper;
    private final TransactionService transactionService;
    private final StatementPdfService statementPdfService;
    private final StatementS3Service statementS3Service;

    public String createStatement(String username) throws IOException {

        Long userId = userMapper.findUserIdByUsername(username);

        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다.");
        }

        List<TransactionDto> transactions =
                transactionService.getMyTransactions(username);

        if (transactions.isEmpty()) {
            throw new IllegalStateException(
                    "거래내역이 없어 명세서를 생성할 수 없습니다.");
        }

        byte[] pdf = statementPdfService.generate(
                username, transactions);

        return statementS3Service.uploadPdf(userId, pdf);
    }

    public byte[] downloadStatement(
            String username,
            String statementId) {

        Long userId = userMapper.findUserIdByUsername(username);

        if (userId == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다.");
        }

        // UUID 형식 검증 및 경로 조작 방지
        java.util.UUID uuid;

        try {
            uuid = java.util.UUID.fromString(statementId);

            if (!uuid.toString().equals(statementId)) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "올바르지 않은 명세서 ID입니다.");
        }

        // 인증된 사용자 ID로만 S3 경로 생성
        String key = "statements/"
                + userId + "/"
                + uuid + ".pdf";

        return statementS3Service.downloadPdf(key);
    }

}
