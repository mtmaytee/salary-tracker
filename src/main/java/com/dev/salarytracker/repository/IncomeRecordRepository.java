package com.dev.salarytracker.repository;

import com.dev.salarytracker.entity.IncomeRecord;
import com.dev.salarytracker.dto.MonthlySummaryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IncomeRecordRepository extends JpaRepository<IncomeRecord, UUID> {
    // ค้นหารายการรายได้ทั้งหมดของ User คนนั้นๆ
    List<IncomeRecord> findByUserId(UUID userId);

    // ค้นหารายได้แยกตามงวดเดือน (เช่น ดึงของเดือน "2026-05" ทั้งหมดมาโชว์ใน Angular)
    List<IncomeRecord> findByUserIdAndPeriodMonth(UUID userId, String periodMonth);

    // 🌟 API สรุปยอดรวมแยกตามรายเดือน (รวมยอดค้างจ่าย)
    @Query("SELECT new com.dev.salarytracker.dto.MonthlySummaryResponse(" +
           "i.periodMonth, " +
           "SUM(CASE WHEN i.paymentStatus = 'PAID' THEN i.grossIncome ELSE 0 END), " +
           "SUM(CASE WHEN i.paymentStatus = 'PAID' THEN i.netIncome ELSE 0 END), " +
           "SUM(CASE WHEN i.paymentStatus = 'PENDING' THEN i.netIncome ELSE 0 END), " +
           "COUNT(i)) " +
           "FROM IncomeRecord i " +
           "WHERE i.user.id = :userId " +
           "GROUP BY i.periodMonth " +
           "ORDER BY i.periodMonth DESC")
    List<MonthlySummaryResponse> getMonthlySummary(@Param("userId") UUID userId);
}
