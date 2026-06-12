package com.dev.salarytracker.service;

import com.dev.salarytracker.dto.InterestCalculationResponse;
import com.dev.salarytracker.entity.IncomeRecord;
import com.dev.salarytracker.repository.IncomeRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class InterestCalculationService {

    @Autowired
    private IncomeRecordRepository incomeRecordRepository;

    private static final double DEFAULT_INTEREST_RATE = 0.15; // 15% ต่อปี ตามกฎหมายแรงงาน

    public InterestCalculationResponse calculateInterest(UUID incomeRecordId) {
        IncomeRecord record = incomeRecordRepository.findById(incomeRecordId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลรายได้ที่ระบุ"));

        LocalDate dueDate = record.getDueDate();
        if (dueDate == null) {
            throw new IllegalStateException("รายการนี้ไม่มีการระบุวันกำหนดจ่าย (Due Date)");
        }

        LocalDate endDate = "PAID".equalsIgnoreCase(record.getPaymentStatus()) 
                ? record.getPaymentDate() 
                : LocalDate.now();

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        long daysLate = 0;
        BigDecimal interestAmount = BigDecimal.ZERO;

        if (endDate.isAfter(dueDate)) {
            daysLate = ChronoUnit.DAYS.between(dueDate, endDate);
            
            // สูตร: ดอกเบี้ย = เงินต้น * อัตราดอกเบี้ย * (จำนวนวัน / 365)
            interestAmount = record.getGrossIncome()
                    .multiply(BigDecimal.valueOf(DEFAULT_INTEREST_RATE))
                    .multiply(BigDecimal.valueOf(daysLate))
                    .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        }

        return InterestCalculationResponse.builder()
                .incomeRecordId(record.getId().toString())
                .principal(record.getGrossIncome())
                .dueDate(dueDate)
                .paymentDate(record.getPaymentDate())
                .daysLate(daysLate)
                .interestRate(DEFAULT_INTEREST_RATE)
                .interestAmount(interestAmount)
                .totalAmount(record.getGrossIncome().add(interestAmount))
                .status(record.getPaymentStatus())
                .build();
    }
}
