package com.example.ai_spell_check.service;


import com.example.ai_spell_check.model.CorrectionHistory;
import com.example.ai_spell_check.model.Language;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.model.enums.ItemType;
import com.example.ai_spell_check.model.response.TextEntryResponse;
import com.example.ai_spell_check.repository.CorrectionHistoryRepository;
import com.example.ai_spell_check.repository.LanguageCodeRepository;
import com.example.ai_spell_check.repository.TextEntryRepository;
import com.example.ai_spell_check.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TextEntryService {
    private final TextEntryRepository textEntryRepository;
    private final UserRepository userRepository;
    private final LanguageCodeRepository languageCodeRepository;
    private final OpenAIService openAIService;

    private final CorrectionHistoryRepository correctionHistoryRepository;

    public TextEntryService(TextEntryRepository textEntryRepository, UserRepository userRepository, LanguageCodeRepository languageCodeRepository, OpenAIService openAIService, CorrectionHistoryRepository correctionHistoryRepository) {
        this.textEntryRepository = textEntryRepository;
        this.userRepository = userRepository;
        this.languageCodeRepository = languageCodeRepository;
        this.openAIService = openAIService;
        this.correctionHistoryRepository = correctionHistoryRepository;
    }

    public TextEntryResponse processTextEntry(String textInput, UserDetails user, String languageCode) throws JsonProcessingException {
        TextEntryResponse response = openAIService.returnCorrectedText(textInput,languageCode);
        Optional<User> userObj = userRepository.findByUsername(user.getUsername());
        Language language = languageCodeRepository.findByCode(languageCode);

        TextEntry textEntry = new TextEntry(language, textInput, response.getCorrectedText(), userObj.get(), response.isCorrect());
        textEntryRepository.save(textEntry);

        CorrectionHistory correctionHistory = new CorrectionHistory(userObj.get(),textEntry.getId(), ItemType.TEXT_ENTRY);
        correctionHistoryRepository.save(correctionHistory);

        return response;
    }
}
