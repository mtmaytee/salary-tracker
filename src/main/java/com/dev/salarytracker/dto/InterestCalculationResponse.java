package com.dev.salarytracker.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class InterestCalculationResponse {
    private String incomeRecordId;
    private BigDecimal principal;
    private LocalDate dueDate;
    private LocalDate paymentDate;
    private Long daysLate;
    private Double interestRate;
    private BigDecimal interestAmount;
    private BigDecimal totalAmount;
    private String status; // PAID, PENDING
}
