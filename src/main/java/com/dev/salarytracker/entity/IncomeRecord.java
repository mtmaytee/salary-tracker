package com.dev.salarytracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Generated;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class IncomeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR) // เพิ่มบรรทัดนี้ เพื่อบอกให้ Hibernate ส่งค่าเป็น String
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)") // บังคับให้ MySQL เก็บเป็นตัวอักษร 36 หลัก
    private UUID id; // เปลี่ยนจาก Integer เป็น java.util.UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 🌟 เปลี่ยนจาก String เป็นการเชื่อมกับ Company Entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private String sourceName;
    private String incomeType; // เช่น "SALARY", "FREELANCE"

    private String periodMonth;
    private Integer installment;

    @Column(nullable = false)
    private String paymentStatus = "PAID"; // PAID, PENDING (ค้างจ่าย)

    // ใช้ BigDecimal สำหรับตัวเงินเพื่อความแม่นยำในการคำนวณ
    private BigDecimal grossIncome;
    private BigDecimal taxDeduction;
    private BigDecimal socialSecurity;
    private BigDecimal otherDeductions;
    private BigDecimal netIncome;

    private LocalDate paymentDate;
    private LocalDate dueDate;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    @Column(updatable = false)
    private Date createDate;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Bangkok")
    private Date modifiedDate;

}
