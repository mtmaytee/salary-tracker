package com.dev.salarytracker.controller;

import com.dev.salarytracker.dto.MessageResponse;
import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.dev.salarytracker.service.ReportService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ReportService reportService;

    // 🌟 4. จัดการไฟล์รายงาน
    @GetMapping("/reports")
    public ResponseEntity<?> listAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @PostMapping("/reports/upload")
    public ResponseEntity<?> uploadReport(
            @RequestParam("file") MultipartFile file,
            @RequestParam("reportCode") String reportCode,
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam(value = "description", required = false) String description) {
        try {
            var report = reportService.saveReport(reportCode, name, category, description, file);
            return ResponseEntity.ok(report);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new MessageResponse("เกิดข้อผิดพลาด: " + e.getMessage()));
        }
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable UUID id) {
        try {
            reportService.deleteReport(id);
            return ResponseEntity.ok(new MessageResponse("ลบรายงานเรียบร้อยแล้ว"));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new MessageResponse("ไม่สามารถลบไฟล์ได้: " + e.getMessage()));
        }
    }

    // 1. ดึงรายชื่อผู้ใช้งานทั้งหมด
    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(usersRepository.findAll());
    }

    // 2. ปรับเปลี่ยน Role ของผู้ใช้งาน
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable UUID id, @RequestParam String role) {
        if (!role.equals("ROLE_USER") && !role.equals("ROLE_ADMIN")) {
            return ResponseEntity.badRequest().body(new MessageResponse("Role ไม่ถูกต้อง (ต้องเป็น ROLE_USER หรือ ROLE_ADMIN)"));
        }

        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบผู้ใช้งาน"));
        
        user.setRole(role);
        usersRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("อัปเดตสิทธิ์การใช้งานเป็น " + role + " เรียบร้อยแล้ว"));
    }

    // 3. ปิด/เปิด การใช้งานบัญชี (Active Status)
    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable UUID id, @RequestParam Boolean active) {
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบผู้ใช้งาน"));
        
        user.setActiveStatus(active);
        usersRepository.save(user);
        
        return ResponseEntity.ok(new MessageResponse("อัปเดตสถานะการใช้งานเรียบร้อยแล้ว"));
    }
}
