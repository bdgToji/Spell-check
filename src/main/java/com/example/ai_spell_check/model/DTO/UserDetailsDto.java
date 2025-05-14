package com.example.ai_spell_check.model.DTO;

import com.example.ai_spell_check.model.CorrectionHistory;
import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.model.enums.FileType;
import com.example.ai_spell_check.model.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserDetailsDto {
    private User user;
    private Page<CorrectionHistoryDto> correctionHistoryPage;
}
