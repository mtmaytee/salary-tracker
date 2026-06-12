package com.dev.salarytracker.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import com.dev.salarytracker.entity.ReportDefinition;
import com.dev.salarytracker.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ReportRepository reportRepository;

    @Value("${app.reports.path}")
    private String reportsPath;

    /**
     * Get all report definitions.
     */
    public List<ReportDefinition> getAllReports() {
        return reportRepository.findAll();
    }

    /**
     * Add or update a report definition and upload the file.
     */
    public ReportDefinition saveReport(String reportCode, String name, String category, String description, MultipartFile file) throws IOException {
        String fileName = uploadReport(file);
        
        ReportDefinition report = reportRepository.findByReportCode(reportCode)
                .orElse(new ReportDefinition());
        
        report.setReportCode(reportCode);
        report.setName(name);
        report.setCategory(category);
        report.setDescription(description);
        report.setFileName(fileName);
        
        return reportRepository.save(report);
    }

    /**
     * Delete a report definition and its physical file.
     */
    public void deleteReport(UUID id) throws IOException {
        ReportDefinition report = reportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบข้อมูลรายงาน"));
        
        // Delete physical file
        Path filePath = Paths.get(reportsPath).resolve(report.getFileName());
        Files.deleteIfExists(filePath);
        
        // Delete database record
        reportRepository.delete(report);
    }

    /**
     * Save an uploaded .jasper file.
     */
    public String uploadReport(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.endsWith(".jasper")) {
            throw new IllegalArgumentException("ต้องเป็นไฟล์ .jasper เท่านั้น");
        }

        Path uploadPath = Paths.get(reportsPath);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

    /**
     * Generate a PDF report using a report code.
     */
    public byte[] exportToPdfByCode(String reportCode, Map<String, Object> parameters) {
        ReportDefinition report = reportRepository.findByReportCode(reportCode)
                .orElseThrow(() -> new RuntimeException("ไม่พบรายงานรหัส: " + reportCode));
        
        return exportToPdf(report.getFileName().replace(".jasper", ""), parameters);
    }

    /**
     * Generate a PDF report using a compiled .jasper file name.
     */
    public byte[] exportToPdf(String reportFileName, Map<String, Object> parameters) {
        try {
            File reportFile = new File(reportsPath, reportFileName + ".jasper");
            InputStream inputStream;
            
            if (reportFile.exists()) {
                inputStream = new FileInputStream(reportFile);
            } else {
                ClassPathResource resource = new ClassPathResource("reports/compiled/" + reportFileName + ".jasper");
                if (!resource.exists()) {
                    throw new RuntimeException("Report file not found: " + reportFileName + ".jasper");
                }
                inputStream = resource.getInputStream();
            }

            try (inputStream;
                 Connection connection = dataSource.getConnection()) {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
                return JasperExportManager.exportReportToPdf(jasperPrint);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating report: " + e.getMessage(), e);
        }
    }
}
