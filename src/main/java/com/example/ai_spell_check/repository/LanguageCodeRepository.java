package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageCodeRepository extends JpaRepository<Language,Long> {
    Language findByCode(String code);
}
