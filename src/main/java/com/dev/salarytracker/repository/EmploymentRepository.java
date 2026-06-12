package com.dev.salarytracker.repository;

import com.dev.salarytracker.entity.Employment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, UUID> {
    List<Employment> findByUserId(UUID userId);
    List<Employment> findByUserIdAndIsActiveTrue(UUID userId);
}
