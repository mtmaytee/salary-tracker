package com.dev.salarytracker.controller;

import com.dev.salarytracker.dto.InterestCalculationResponse;
import com.dev.salarytracker.service.InterestCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/interest")
public class InterestCalculationController {

    @Autowired
    private InterestCalculationService interestCalculationService;

    @GetMapping("/calculate/{incomeRecordId}")
    public ResponseEntity<InterestCalculationResponse> calculateInterest(@PathVariable UUID incomeRecordId) {
        return ResponseEntity.ok(interestCalculationService.calculateInterest(incomeRecordId));
    }
}
