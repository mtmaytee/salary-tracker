package com.dev.salarytracker.service;

import com.dev.salarytracker.dto.EmploymentRequest;
import com.dev.salarytracker.entity.Company;
import com.dev.salarytracker.entity.Employment;
import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.repository.CompanyRepository;
import com.dev.salarytracker.repository.EmploymentRepository;
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
public class EmploymentService {

    @Autowired
    private EmploymentRepository employmentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TaxCalculationService taxCalculationService;

    private Users getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("กรุณาเข้าสู่ระบบก่อนทำรายการ");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Users) {
            return (Users) principal;
        }
        String username = (principal instanceof org.springframework.security.core.userdetails.UserDetails) 
            ? ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername() 
            : principal.toString();
        
        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ไม่พบผู้ใช้งานในระบบ"));
    }

    public Employment createEmployment(EmploymentRequest request) {
        Users currentUser = getCurrentUser();
        Company company = companyRepository.findById(UUID.fromString(request.getCompanyId()))
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลบริษัท"));

        Employment employment = new Employment();
        employment.setUser(currentUser);
        employment.setCompany(company);
        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setExpectedSso(request.getExpectedSso() != null ? request.getExpectedSso() : BigDecimal.ZERO);
        
        BigDecimal calculatedTax = taxCalculationService.calculateMonthlyWithholdingTax(request.getBaseSalary());
        employment.setExpectedTax(request.getExpectedTax() != null ? request.getExpectedTax() : calculatedTax);
        
        employment.setSalaryPaymentDay(request.getSalaryPaymentDay());
        employment.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        return employmentRepository.save(employment);
    }

    public List<Employment> getMyEmployments() {
        Users currentUser = getCurrentUser();
        return employmentRepository.findByUserId(currentUser.getId());
    }

    public Employment updateEmployment(UUID id, EmploymentRequest request) {
        Users currentUser = getCurrentUser();
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลสัญญาจ้าง"));

        if (!employment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("ไม่มีสิทธิ์แก้ไขข้อมูลนี้");
        }

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(UUID.fromString(request.getCompanyId()))
                    .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลบริษัท"));
            employment.setCompany(company);
        }

        employment.setPosition(request.getPosition());
        employment.setBaseSalary(request.getBaseSalary());
        employment.setExpectedSso(request.getExpectedSso());
        
        if (request.getExpectedTax() == null) {
            employment.setExpectedTax(taxCalculationService.calculateMonthlyWithholdingTax(request.getBaseSalary()));
        } else {
            employment.setExpectedTax(request.getExpectedTax());
        }
        
        employment.setSalaryPaymentDay(request.getSalaryPaymentDay());
        employment.setIsActive(request.getIsActive());

        return employmentRepository.save(employment);
    }

    public void deleteEmployment(UUID id) {
        Users currentUser = getCurrentUser();
        Employment employment = employmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลสัญญาจ้าง"));

        if (!employment.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("ไม่มีสิทธิ์ลบข้อมูลนี้");
        }

        employmentRepository.delete(employment);
    }
}
