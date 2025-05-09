package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.CorrectionHistory;
import com.example.ai_spell_check.model.DTO.CorrectionHistoryDto;
import com.example.ai_spell_check.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectionHistoryRepository extends JpaRepository<CorrectionHistory,Long> {
    Page<CorrectionHistoryDto> findCombinedHistoryByUser(String userId, Pageable pageable);
}
