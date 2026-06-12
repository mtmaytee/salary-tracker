package com.dev.salarytracker.controller;

import com.dev.salarytracker.dto.MessageResponse;
import com.dev.salarytracker.entity.Company;
import com.dev.salarytracker.service.IncomeModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies") // 🌟 ใช้ Path ของตาราง Company ตรงๆ
public class CompanyController {
    @Autowired
    private IncomeModuleService incomeModuleService;

    // 1. API ดึงรายชื่อบริษัททั้งหมดในระบบ
    @GetMapping
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(incomeModuleService.getAllCompanies());
    }

    // 2. API เพิ่มบริษัท/ผู้ว่าจ้างใหม่
    @PostMapping
    public ResponseEntity<Company> addCompany(@RequestBody Company company) {
        Company newCompany = incomeModuleService.createCompany(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);
    }

    // 3. API แก้ไขข้อมูลบริษัท
    @PutMapping("/{id}")
    public ResponseEntity<Company> updateCompany(@PathVariable UUID id, @RequestBody Company company) {
        return ResponseEntity.ok(incomeModuleService.updateCompany(id, company));
    }

    // 4. API ลบบริษัท
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable UUID id) {
        incomeModuleService.deleteCompany(id);
        return ResponseEntity.ok(new MessageResponse("ลบข้อมูลบริษัทเรียบร้อยแล้ว"));
    }
}
