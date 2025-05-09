package com.example.ai_spell_check.model.DTO;

import com.example.ai_spell_check.model.enums.FileType;
import com.example.ai_spell_check.model.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CorrectionHistoryDto {
    private Long itemId;              // Document/TextEntry ID
    private ItemType itemType;        // DOCUMENT or TEXT_ENTRY
    private String title;             // Title (for TextEntry) or filename (for Document)
    private LocalDateTime date;       // Upload date or creation date
    private String languageName;      // Language name
    private boolean isCorrect;        // Correction status
    private String previewContent;    // Short preview of content (for TextEntry)
    private FileType fileType;        // For Document only
}
