package com.dev.salarytracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class IncomeRecordRequest {
    @NotBlank(message = "กรุณาเลือกบริษัทหรือผู้ว่าจ้าง")
    private String companyId;

    @NotBlank(message = "กรุณาระบุประเภทรายได้")
    private String incomeType; // SALARY, FREELANCE, BONUS

    @NotBlank(message = "กรุณาระบุงวดประจำเดือน")
    private String periodMonth; // รูปแบบ "YYYY-MM" เช่น "2026-05"

    private Integer installment; // งวดที่ 1, 2 (Optional)

    private String paymentStatus; // PAID, PENDING

    @NotNull(message = "กรุณากรอกยอดเงินได้รวมก่อนหัก")
    private BigDecimal grossIncome;

    private BigDecimal taxDeduction;   // ถ้าไม่กรอก จะเซตเป็น 0 ใน Service
    private BigDecimal socialSecurity;  // ถ้าไม่กรอก จะเซตเป็น 0 ใน Service
    private BigDecimal otherDeductions; // ถ้าไม่กรอก จะเซตเป็น 0 ใน Service

    private LocalDate paymentDate; // Optional สำหรับ PENDING
    private LocalDate dueDate;     // Optional
}
