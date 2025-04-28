package com.example.ai_spell_check.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentUploadResponse {
    private boolean isCorrect;
    private String correctedContent;
    private String fileName;
}