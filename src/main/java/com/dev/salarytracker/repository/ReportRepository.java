package com.dev.salarytracker.repository;

import com.dev.salarytracker.entity.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<ReportDefinition, UUID> {
    Optional<ReportDefinition> findByReportCode(String reportCode);
}
