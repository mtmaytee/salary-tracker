package com.dev.salarytracker.controller;

import com.dev.salarytracker.dto.IncomeRecordRequest;
import com.dev.salarytracker.dto.MessageResponse;
import com.dev.salarytracker.dto.MonthlySummaryResponse;
import com.dev.salarytracker.entity.IncomeRecord;
import com.dev.salarytracker.service.IncomeModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/income-records") // 🌟 ปรับให้เจาะจงกับตารางรายได้
public class IncomeRecordController {

    @Autowired
    private IncomeModuleService incomeModuleService;

    // 1. API บันทึกเงินเดือน/รายรับ (Gross -> Net อัตโนมัติ)
    @PostMapping
    public ResponseEntity<IncomeRecord> saveIncomeRecord(@Valid @RequestBody IncomeRecordRequest request) {
        IncomeRecord savedRecord = incomeModuleService.createIncomeRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
    }

    // 2. API ดึงประวัติรายได้ทั้งหมด (Filter งวดเดือนได้ เช่น ?month=2026-05)
    @GetMapping
    public ResponseEntity<List<IncomeRecord>> getMyIncomeHistory(@RequestParam(required = false, name = "month") String periodMonth) {
        List<IncomeRecord> history = incomeModuleService.getMyIncomeRecords(periodMonth);
        return ResponseEntity.ok(history);
    }

    // 3. API สรุปยอดรวมรายเดือน (ใช้ทำ Dashboard/Chart)
    @GetMapping("/summary")
    public ResponseEntity<List<MonthlySummaryResponse>> getMonthlySummary() {
        return ResponseEntity.ok(incomeModuleService.getMonthlySummary());
    }

    // 4. API แก้ไขข้อมูลรายได้ (ใช้สำหรับเปลี่ยนจาก PENDING เป็น PAID ได้ด้วย)
    @PutMapping("/{id}")
    public ResponseEntity<IncomeRecord> updateIncomeRecord(@PathVariable UUID id, @Valid @RequestBody IncomeRecordRequest request) {
        return ResponseEntity.ok(incomeModuleService.updateIncomeRecord(id, request));
    }

    // 5. API ลบรายการรายได้
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncomeRecord(@PathVariable UUID id) {
        incomeModuleService.deleteIncomeRecord(id);
        return ResponseEntity.ok(new MessageResponse("ลบรายการรายได้เรียบร้อยแล้ว"));
    }
}