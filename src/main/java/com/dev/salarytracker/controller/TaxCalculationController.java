package com.dev.salarytracker.controller;

import com.dev.salarytracker.entity.TaxBracket;
import com.dev.salarytracker.entity.TaxMasterData;
import com.dev.salarytracker.repository.TaxBracketRepository;
import com.dev.salarytracker.repository.TaxMasterDataRepository;
import com.dev.salarytracker.service.TaxCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tax")
public class TaxCalculationController {

    @Autowired
    private TaxCalculationService taxCalculationService;

    @Autowired
    private TaxMasterDataRepository taxMasterDataRepository;

    @Autowired
    private TaxBracketRepository taxBracketRepository;

    @GetMapping("/master-data")
    public ResponseEntity<TaxMasterData> getMasterData() {
        return ResponseEntity.ok(taxMasterDataRepository.findFirstByIsActiveTrue()
                .orElseThrow(() -> new RuntimeException("Tax Master Data not configured")));
    }

    @GetMapping("/brackets")
    public ResponseEntity<List<TaxBracket>> getBrackets() {
        return ResponseEntity.ok(taxBracketRepository.findAllByOrderBySequenceAsc());
    }

    @PostMapping("/calculate")
    public ResponseEntity<Map<String, BigDecimal>> calculateTax(@RequestBody Map<String, BigDecimal> request) {
        BigDecimal salary = request.get("salary");
        BigDecimal monthlyTax = taxCalculationService.calculateMonthlyWithholdingTax(salary);
        return ResponseEntity.ok(Map.of("monthlyWithholdingTax", monthlyTax));
    }

    // ================= CRUD Endpoints for Tax Rules =================

    @PutMapping("/master-data/{id}")
    public ResponseEntity<TaxMasterData> updateMasterData(@PathVariable UUID id, @RequestBody TaxMasterData masterData) {
        return ResponseEntity.ok(taxCalculationService.updateMasterData(id, masterData));
    }

    @PostMapping("/brackets")
    public ResponseEntity<TaxBracket> createBracket(@RequestBody TaxBracket bracket) {
        return ResponseEntity.ok(taxCalculationService.createBracket(bracket));
    }

    @PutMapping("/brackets/{id}")
    public ResponseEntity<TaxBracket> updateBracket(@PathVariable UUID id, @RequestBody TaxBracket bracket) {
        return ResponseEntity.ok(taxCalculationService.updateBracket(id, bracket));
    }

    @DeleteMapping("/brackets/{id}")
    public ResponseEntity<?> deleteBracket(@PathVariable UUID id) {
        taxCalculationService.deleteBracket(id);
        return ResponseEntity.ok().build();
    }
}
