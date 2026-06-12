package com.dev.salarytracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummaryResponse {
    private String periodMonth;    // เช่น "2026-05"
    private BigDecimal totalGross; // ยอดรวมก่อนหัก
    private BigDecimal totalNet;   // ยอดรวมสุทธิ
    private BigDecimal totalPending; // 🌟 ยอดค้างจ่ายสะสมของเดือนนี้
    private Long recordCount;      // จำนวนรายการในเดือนนั้น
}
