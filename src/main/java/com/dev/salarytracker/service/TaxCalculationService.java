package com.dev.salarytracker.service;

import com.dev.salarytracker.entity.TaxBracket;
import com.dev.salarytracker.entity.TaxMasterData;
import com.dev.salarytracker.repository.TaxBracketRepository;
import com.dev.salarytracker.repository.TaxMasterDataRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TaxCalculationService {

    @Autowired
    private TaxMasterDataRepository taxMasterDataRepository;

    @Autowired
    private TaxBracketRepository taxBracketRepository;

    @PostConstruct
    public void initDefaults() {
        if (taxMasterDataRepository.count() == 0) {
            TaxMasterData master = new TaxMasterData();
            master.setMaxPersonalExpenses(new BigDecimal("100000"));
            master.setPersonalAllowance(new BigDecimal("60000"));
            master.setMaxSocialSecurity(new BigDecimal("9000"));
            master.setActive(true);
            taxMasterDataRepository.save(master);
        }

        if (taxBracketRepository.count() == 0) {
            List<TaxBracket> brackets = new ArrayList<>();
            brackets.add(createBracket(0, 150000, 0, 1));
            brackets.add(createBracket(150001, 300000, 5, 2));
            brackets.add(createBracket(300001, 500000, 10, 3));
            brackets.add(createBracket(500001, 750000, 15, 4));
            brackets.add(createBracket(750001, 1000000, 20, 5));
            brackets.add(createBracket(1000001, 2000000, 25, 6));
            brackets.add(createBracket(2000001, 5000000, 30, 7));
            brackets.add(createBracket(5000001, -1, 35, 8)); // -1 for no limit
            taxBracketRepository.saveAll(brackets);
        }
    }

    private TaxBracket createBracket(long min, long max, double rate, int seq) {
        TaxBracket b = new TaxBracket();
        b.setMinIncome(BigDecimal.valueOf(min));
        if (max != -1) b.setMaxIncome(BigDecimal.valueOf(max));
        b.setTaxRate(BigDecimal.valueOf(rate));
        b.setSequence(seq);
        return b;
    }

    public BigDecimal calculateMonthlyWithholdingTax(BigDecimal monthlySalary) {
        if (monthlySalary == null || monthlySalary.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        TaxMasterData master = taxMasterDataRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Tax Master Data not configured"));

        List<TaxBracket> brackets = taxBracketRepository.findAllByOrderBySequenceAsc();

        // 1. Annual Gross Income
        BigDecimal annualGross = monthlySalary.multiply(BigDecimal.valueOf(12));

        // 2. Deductions
        // Standard expenses: 50% of income but not exceeding 100k
        BigDecimal expenses = annualGross.multiply(new BigDecimal("0.5"));
        if (expenses.compareTo(master.getMaxPersonalExpenses()) > 0) {
            expenses = master.getMaxPersonalExpenses();
        }

        // Net Income = Gross - Expenses - Personal Allowance - SSO
        BigDecimal netIncome = annualGross
                .subtract(expenses)
                .subtract(master.getPersonalAllowance())
                .subtract(master.getMaxSocialSecurity());

        if (netIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 3. Progressive Tax Calculation
        BigDecimal totalTax = BigDecimal.ZERO;
        BigDecimal remainingIncome = netIncome;

        for (TaxBracket bracket : brackets) {
            BigDecimal min = bracket.getMinIncome();
            BigDecimal max = bracket.getMaxIncome();
            BigDecimal rate = bracket.getTaxRate().divide(BigDecimal.valueOf(100));

            BigDecimal taxableInThisBracket;
            if (max == null) {
                // Last bracket
                taxableInThisBracket = remainingIncome.subtract(min.subtract(BigDecimal.ONE));
            } else {
                BigDecimal bracketRange = max.subtract(min.subtract(BigDecimal.ONE));
                if (remainingIncome.compareTo(max) > 0) {
                    taxableInThisBracket = bracketRange;
                } else if (remainingIncome.compareTo(min) >= 0) {
                    taxableInThisBracket = remainingIncome.subtract(min.subtract(BigDecimal.ONE));
                } else {
                    taxableInThisBracket = BigDecimal.ZERO;
                }
            }

            if (taxableInThisBracket.compareTo(BigDecimal.ZERO) > 0) {
                totalTax = totalTax.add(taxableInThisBracket.multiply(rate));
            }
        }

        // 4. Monthly Tax
        return totalTax.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
    }

    // ================= Management Methods (For Admin/Angular CRUD) =================

    public TaxMasterData updateMasterData(UUID id, TaxMasterData updatedData) {
        TaxMasterData existing = taxMasterDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูล Master Data"));

        existing.setMaxPersonalExpenses(updatedData.getMaxPersonalExpenses());
        existing.setPersonalAllowance(updatedData.getPersonalAllowance());
        existing.setMaxSocialSecurity(updatedData.getMaxSocialSecurity());
        existing.setActive(updatedData.isActive());

        return taxMasterDataRepository.save(existing);
    }

    public TaxBracket updateBracket(UUID id, TaxBracket updatedData) {
        TaxBracket existing = taxBracketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลขั้นบันไดภาษี"));

        existing.setMinIncome(updatedData.getMinIncome());
        existing.setMaxIncome(updatedData.getMaxIncome());
        existing.setTaxRate(updatedData.getTaxRate());
        existing.setSequence(updatedData.getSequence());

        return taxBracketRepository.save(existing);
    }

    public TaxBracket createBracket(TaxBracket bracket) {
        return taxBracketRepository.save(bracket);
    }

    public void deleteBracket(UUID id) {
        taxBracketRepository.deleteById(id);
    }
}
