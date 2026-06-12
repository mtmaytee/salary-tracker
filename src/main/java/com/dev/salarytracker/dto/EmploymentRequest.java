package com.dev.salarytracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmploymentRequest {
    @NotBlank(message = "กรุณาเลือกบริษัท")
    private String companyId;

    private String position;

    @NotNull(message = "กรุณาระบุฐานเงินเดือน")
    private BigDecimal baseSalary;

    private BigDecimal expectedSso;
    private BigDecimal expectedTax;

    private Integer salaryPaymentDay;

    private Boolean isActive;
}
