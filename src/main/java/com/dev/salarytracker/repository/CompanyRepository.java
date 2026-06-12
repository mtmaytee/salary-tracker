package com.dev.salarytracker.repository;

import com.dev.salarytracker.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
    // ตอนนี้ใช้ findAll() พื้นฐานจาก JpaRepository ได้เลย เพราะเป็นข้อมูลกลาง
}
