package com.dev.salarytracker.controller;

import com.dev.salarytracker.dto.EmploymentRequest;
import com.dev.salarytracker.dto.MessageResponse;
import com.dev.salarytracker.entity.Employment;
import com.dev.salarytracker.service.EmploymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employments")
public class EmploymentController {

    @Autowired
    private EmploymentService employmentService;

    @PostMapping
    public ResponseEntity<Employment> createEmployment(@Valid @RequestBody EmploymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employmentService.createEmployment(request));
    }

    @GetMapping
    public ResponseEntity<List<Employment>> getMyEmployments() {
        return ResponseEntity.ok(employmentService.getMyEmployments());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employment> updateEmployment(@PathVariable UUID id, @Valid @RequestBody EmploymentRequest request) {
        return ResponseEntity.ok(employmentService.updateEmployment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployment(@PathVariable UUID id) {
        employmentService.deleteEmployment(id);
        return ResponseEntity.ok(new MessageResponse("ลบข้อมูลสัญญาจ้างเรียบร้อยแล้ว"));
    }
}
