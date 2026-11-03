package com.finova.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CategoryStatisticsDto {

    private String category;
    private BigDecimal totalAmount;
    private Long transactionCount;
}