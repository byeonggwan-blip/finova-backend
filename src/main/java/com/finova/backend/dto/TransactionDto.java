package com.finova.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDto {

    private Long transactionId;
    private Long userId;
    private String transactionType;
    private String category;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
}