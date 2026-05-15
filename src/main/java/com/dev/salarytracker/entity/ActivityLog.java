package com.dev.salarytracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR) // เพิ่มบรรทัดนี้ เพื่อบอกให้ Hibernate ส่งค่าเป็น String
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)") // บังคับให้ MySQL เก็บเป็นตัวอักษร 36 หลัก
    private UUID id;

    private String action;//กิจกรรมที่ทำ (เช่น "LOGIN", "CREATE_SALARY", "DELETE_USER")

    @Column(columnDefinition = "TEXT")
    private String details;//รายละเอียดเพิ่มเติม (เช่น "ลบข้อมูลเงินเดือนของ ID: ...")

    private String username;

    private String ipAddress;

    private String status;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    @Column(updatable = false)
    private LocalDateTime timestamp;
}
