package com.example.ai_spell_check.model.DTO;

import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDetailsDTO {
    private User user;
    private Page<Document> documentsPage;
    private Page<TextEntry> textEntriesPage;
}
