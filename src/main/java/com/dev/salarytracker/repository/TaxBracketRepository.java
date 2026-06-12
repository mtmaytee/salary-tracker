package com.dev.salarytracker.repository;

import com.dev.salarytracker.entity.TaxBracket;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface TaxBracketRepository extends JpaRepository<TaxBracket, UUID> {
    List<TaxBracket> findAllByOrderBySequenceAsc();
}
