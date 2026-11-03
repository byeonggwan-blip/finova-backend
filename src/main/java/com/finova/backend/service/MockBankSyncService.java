package com.finova.backend.service;

import com.finova.backend.dto.MockBankTransactionDto;
import com.finova.backend.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MockBankSyncService {

    private final TransactionMapper transactionMapper;
    private final JsonMapper jsonMapper;

    @Transactional
    public int syncTransactions(String username) throws Exception {

        ClassPathResource resource =
                new ClassPathResource("mock-bank/transactions.json");

        int insertedCount = 0;

        try (InputStream inputStream = resource.getInputStream()) {

            JsonNode root = jsonMapper.readTree(inputStream);

            String bankCode = root.get("bankCode").asText();
            String accountId = root.get("accountId").asText();

            // 로그인 사용자의 계좌 소유권 확인
            int ownedCount = transactionMapper.countOwnedBankAccount(
                    username,
                    accountId,
                    bankCode
            );

            if (ownedCount == 0) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "해당 금융계좌에 접근할 권한이 없습니다."
                );
            }

            // 소유권 검증에 성공한 경우에만 거래내역 저장
            for (JsonNode node : root.get("transactions")) {

                MockBankTransactionDto transaction =
                        jsonMapper.treeToValue(
                                node, MockBankTransactionDto.class
                        );

                insertedCount += transactionMapper.insertBankTransaction(
                        username,
                        bankCode,
                        transaction
                );
            }
        }

        return insertedCount;
    }
}