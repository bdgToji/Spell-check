package com.example.ai_spell_check.service;


import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.repository.TextEntryRepository;
import com.example.ai_spell_check.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class TextEntryService {
    private final TextEntryRepository textEntryRepository;
    private final UserRepository userRepository;

    public TextEntryService(TextEntryRepository textEntryRepository, UserRepository userRepository) {
        this.textEntryRepository = textEntryRepository;
        this.userRepository = userRepository;
    }

    public void createTextEntry(String username, String originalText, String correctedText, boolean isCorrect){
        Optional<User> user = userRepository.findByUsername(username);
        TextEntry textEntry = new TextEntry(originalText,correctedText,user.get(), isCorrect);
        this.textEntryRepository.save(textEntry);
    }

}
