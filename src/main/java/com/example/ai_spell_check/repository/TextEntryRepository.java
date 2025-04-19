package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.TextEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextEntryRepository extends JpaRepository<TextEntry,Long> {
}
