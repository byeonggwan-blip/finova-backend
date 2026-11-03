package com.finova.backend.service;

import com.finova.backend.dto.TransactionDto;
import com.finova.backend.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionMapper transactionMapper;

    @Transactional(readOnly = true)
    public List<TransactionDto> getMyTransactions(String username) {
        return transactionMapper.findByUsername(username);
    }
}