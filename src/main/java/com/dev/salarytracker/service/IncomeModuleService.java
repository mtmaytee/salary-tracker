package com.dev.salarytracker.service;

import com.dev.salarytracker.dto.IncomeRecordRequest;
import com.dev.salarytracker.dto.MonthlySummaryResponse;
import com.dev.salarytracker.entity.Company;
import com.dev.salarytracker.entity.IncomeRecord;
import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.repository.CompanyRepository;
import com.dev.salarytracker.repository.IncomeRecordRepository;
import com.dev.salarytracker.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class IncomeModuleService {

    @Autowired
    private IncomeRecordRepository incomeRecordRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private com.dev.salarytracker.repository.EmploymentRepository employmentRepository;

    // Helper Method: ดึงข้อมูลผู้ใช้งานที่กำลัง Login อยู่จากสิทธิ์ JWT
    private Users getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("กรุณาเข้าสู่ระบบก่อนทำรายการ");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Users) {
            return (Users) principal;
        }

        String username;
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ไม่พบผู้ใช้งานในระบบด้วยชื่อ: " + username));
    }

    // ================= บันทึกรายได้ =================
    public IncomeRecord createIncomeRecord(IncomeRecordRequest request) {
        Users currentUser = getCurrentUser();

        Company company = companyRepository.findById(UUID.fromString(request.getCompanyId()))
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลบริษัทที่ระบุ"));

        IncomeRecord record = new IncomeRecord();
        record.setUser(currentUser);
        record.setCompany(company);
        record.setIncomeType(request.getIncomeType());
        record.setPeriodMonth(request.getPeriodMonth());
        record.setInstallment(request.getInstallment());
        record.setPaymentDate(request.getPaymentDate());
        record.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "PAID");

        // Set Due Date
        if (request.getDueDate() != null) {
            record.setDueDate(request.getDueDate());
        } else if ("SALARY".equalsIgnoreCase(request.getIncomeType())) {
            // Try to find employment to get payment day
            employmentRepository.findByUserId(currentUser.getId()).stream()
                    .filter(e -> e.getCompany().getId().equals(company.getId()) && e.getIsActive())
                    .findFirst()
                    .ifPresent(e -> {
                        if (e.getSalaryPaymentDay() != null) {
                            try {
                                String[] parts = request.getPeriodMonth().split("-");
                                int year = Integer.parseInt(parts[0]);
                                int month = Integer.parseInt(parts[1]);
                                java.time.YearMonth yearMonth = java.time.YearMonth.of(year, month);
                                int day = Math.min(e.getSalaryPaymentDay(), yearMonth.lengthOfMonth());
                                record.setDueDate(java.time.LocalDate.of(year, month, day));
                            } catch (Exception ex) {
                                log.warn("Cannot calculate due date: {}", ex.getMessage());
                            }
                        }
                    });
        }

        BigDecimal gross = request.getGrossIncome();
        BigDecimal tax = request.getTaxDeduction() != null ? request.getTaxDeduction() : BigDecimal.ZERO;
        BigDecimal social = request.getSocialSecurity() != null ? request.getSocialSecurity() : BigDecimal.ZERO;
        BigDecimal other = request.getOtherDeductions() != null ? request.getOtherDeductions() : BigDecimal.ZERO;

        BigDecimal net = gross.subtract(tax).subtract(social).subtract(other);

        record.setGrossIncome(gross);
        record.setTaxDeduction(tax);
        record.setSocialSecurity(social);
        record.setOtherDeductions(other);
        record.setNetIncome(net);

        return incomeRecordRepository.save(record);
    }

    public List<IncomeRecord> getMyIncomeRecords(String periodMonth) {
        Users currentUser = getCurrentUser();
        if (periodMonth != null && !periodMonth.isBlank()) {
            return incomeRecordRepository.findByUserIdAndPeriodMonth(currentUser.getId(), periodMonth);
        }
        return incomeRecordRepository.findByUserId(currentUser.getId());
    }

    // 🌟 ดึงข้อมูลสรุปรายเดือน
    public List<MonthlySummaryResponse> getMonthlySummary() {
        Users currentUser = getCurrentUser();
        return incomeRecordRepository.getMonthlySummary(currentUser.getId());
    }

    // 🌟 อัปเดตข้อมูลรายได้
    public IncomeRecord updateIncomeRecord(UUID id, IncomeRecordRequest request) {
        Users currentUser = getCurrentUser();
        IncomeRecord record = incomeRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลรายได้ที่ระบุ"));

        // ตรวจสอบสิทธิ์ว่าเป็นของ User คนนั้นจริงไหม
        if (!record.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("ไม่มีสิทธิ์แก้ไขข้อมูลนี้");
        }

        Company company = companyRepository.findById(UUID.fromString(request.getCompanyId()))
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลบริษัทที่ระบุ"));

        record.setCompany(company);
        record.setIncomeType(request.getIncomeType());
        record.setPeriodMonth(request.getPeriodMonth());
        record.setInstallment(request.getInstallment());
        record.setPaymentDate(request.getPaymentDate());
        record.setDueDate(request.getDueDate());
        record.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "PAID");

        BigDecimal gross = request.getGrossIncome();
        BigDecimal tax = request.getTaxDeduction() != null ? request.getTaxDeduction() : BigDecimal.ZERO;
        BigDecimal social = request.getSocialSecurity() != null ? request.getSocialSecurity() : BigDecimal.ZERO;
        BigDecimal other = request.getOtherDeductions() != null ? request.getOtherDeductions() : BigDecimal.ZERO;

        BigDecimal net = gross.subtract(tax).subtract(social).subtract(other);

        record.setGrossIncome(gross);
        record.setTaxDeduction(tax);
        record.setSocialSecurity(social);
        record.setOtherDeductions(other);
        record.setNetIncome(net);

        return incomeRecordRepository.save(record);
    }

    // 🌟 ลบข้อมูลรายได้
    public void deleteIncomeRecord(UUID id) {
        Users currentUser = getCurrentUser();
        IncomeRecord record = incomeRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลรายได้ที่ระบุ"));

        if (!record.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("ไม่มีสิทธิ์ลบข้อมูลนี้");
        }

        incomeRecordRepository.delete(record);
    }

    // ================= จัดการบริษัท =================

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    public Company updateCompany(UUID id, Company updatedCompany) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลบริษัทที่ระบุ"));

        existingCompany.setName(updatedCompany.getName());
        existingCompany.setTaxId(updatedCompany.getTaxId());
        existingCompany.setAddress(updatedCompany.getAddress());

        return companyRepository.save(existingCompany);
    }

    public void deleteCompany(UUID id) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลบริษัทที่ระบุ"));

        companyRepository.delete(existingCompany);
    }
}
