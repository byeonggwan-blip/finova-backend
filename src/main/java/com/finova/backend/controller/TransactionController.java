package com.finova.backend.controller;

import com.finova.backend.dto.TransactionDto;
import com.finova.backend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.finova.backend.service.MockBankSyncService;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final MockBankSyncService mockBankSyncService;

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getMyTransactions(
            Authentication authentication) {

        String username = authentication.getName();

        List<TransactionDto> transactions =
                transactionService.getMyTransactions(username);

        return ResponseEntity.ok(transactions);
    }
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncTransactions(
            Authentication authentication) throws Exception {

        String username = authentication.getName();

        int insertedCount =
                mockBankSyncService.syncTransactions(username);

        return ResponseEntity.ok(Map.of(
                "message", "거래내역 동기화 완료",
                "insertedCount", insertedCount
        ));
    }
}