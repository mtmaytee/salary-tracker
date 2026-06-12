package com.dev.salarytracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tax_master_data")
@Data
public class TaxMasterData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(precision = 19, scale = 2)
    private BigDecimal maxPersonalExpenses; // 100,000

    @Column(precision = 19, scale = 2)
    private BigDecimal personalAllowance; // 60,000

    @Column(precision = 19, scale = 2)
    private BigDecimal maxSocialSecurity; // 9,000

    private boolean isActive = true;
}
