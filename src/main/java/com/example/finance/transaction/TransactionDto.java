package com.example.finance.transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDto(
        Long id,
        BigDecimal amount,
        String category,
        String merchant,
        LocalDate txnDate
) {}