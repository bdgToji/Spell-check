package com.example.ai_spell_check.model.response;

import lombok.*;
@Data
@NoArgsConstructor
public class TextEntryResponse {
    private String title;
    private boolean isCorrect;
    private String correctedText;
}
