package com.dev.salarytracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tax_brackets")
@Data
public class TaxBracket {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID id;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal minIncome;

    @Column(precision = 19, scale = 2)
    private BigDecimal maxIncome; // Null means no limit (last bracket)

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal taxRate; // Percentage (e.g., 5.00)

    private Integer sequence; // Order of brackets
}
