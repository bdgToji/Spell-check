package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.CorrectionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrectionHistoryRepository extends JpaRepository<CorrectionHistory,Long> {
}
