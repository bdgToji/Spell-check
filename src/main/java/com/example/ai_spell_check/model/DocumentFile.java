package com.example.ai_spell_check.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DocumentFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Lob
    private byte[] content;

    public DocumentFile(String fileName, byte[] content) {
        this.fileName = fileName;
        this.content = content;
    }
}
