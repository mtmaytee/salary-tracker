package com.dev.salarytracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR) // เพิ่มบรรทัดนี้ เพื่อบอกให้ Hibernate ส่งค่าเป็น String
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)") // บังคับให้ MySQL เก็บเป็นตัวอักษร 36 หลัก
    private UUID id; // เปลี่ยนจาก Integer เป็น java.util.UUID

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "tax_id", length = 20)
    private String taxId; // เลขประจำตัวผู้เสียภาษีอากร

    @Column(columnDefinition = "TEXT")
    private String address;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    @Column(updatable = false)
    private Date createDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    private Date modifiedDate;

}
