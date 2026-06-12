package com.dev.salarytracker.repository;

import com.dev.salarytracker.entity.TaxMasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface TaxMasterDataRepository extends JpaRepository<TaxMasterData, UUID> {
    Optional<TaxMasterData> findFirstByIsActiveTrue();
}
