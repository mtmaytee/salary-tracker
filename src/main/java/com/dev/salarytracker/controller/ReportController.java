package com.dev.salarytracker.controller;

import com.dev.salarytracker.entity.Users;
import com.dev.salarytracker.repository.UsersRepository;
import com.dev.salarytracker.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private UsersRepository usersRepository;

    private Users getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("กรุณาเข้าสู่ระบบก่อนทำรายการ");
        }

        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof Users) {
            return (Users) principal;
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        return usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ไม่พบผู้ใช้งานในระบบ"));
    }

    /**
     * Get list of available reports.
     */
    @GetMapping("/list")
    public ResponseEntity<?> getAvailableReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    /**
     * Download a report by reportCode.
     * Automatically adds USER_ID parameter from current session.
     */
    @GetMapping("/{reportCode}")
    public ResponseEntity<byte[]> downloadReport(
            @PathVariable String reportCode,
            @RequestParam Map<String, Object> allParams) {

        Users currentUser = getCurrentUser();
        
        // Prepare parameters for Jasper
        Map<String, Object> reportParams = new HashMap<>(allParams);
        reportParams.put("USER_ID", currentUser.getId().toString()); // Pass current user ID

        byte[] pdfReport = reportService.exportToPdfByCode(reportCode, reportParams);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", reportCode + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfReport);
    }
}
