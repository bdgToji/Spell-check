package com.example.ai_spell_check.service;


import com.example.ai_spell_check.model.Language;
import com.example.ai_spell_check.repository.LanguageCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageCodeService {
    private final LanguageCodeRepository languageCodeRepository;

    public LanguageCodeService(LanguageCodeRepository languageCodeRepository) {
        this.languageCodeRepository = languageCodeRepository;
    }

    public List<Language> findAll(){return languageCodeRepository.findAll();}
}
