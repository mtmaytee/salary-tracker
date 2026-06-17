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
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR) // เพิ่มบรรทัดนี้ เพื่อบอกให้ Hibernate ส่งค่าเป็น String
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)") // บังคับให้ MySQL เก็บเป็นตัวอักษร 36 หลัก
    private UUID id; // เปลี่ยนจาก Integer เป็น java.util.UUID

    @Column(unique = true)
    private String nationalId; // Hibernate จะสร้างเป็น national_id ให้อัตโนมัติ

    @Column(unique = true)
    private String phoneNumber;

    private String firstName;
    private String lastName;

    // กำหนดค่าเริ่มต้นให้เป็น true เมื่อสร้างข้อมูลใหม่
    private Boolean activeStatus = true;

    @Column(unique = true)
    private String username;
    private String password;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String role = "ROLE_USER"; // ROLE_USER, ROLE_ADMIN

    private String verificationToken;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    @Column(updatable = false)
    private Date createDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    private Date modifiedDate;

}
