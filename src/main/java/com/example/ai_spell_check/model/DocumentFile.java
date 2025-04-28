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

    private Long fileSize;

    @Lob
    private byte[] content;

    public DocumentFile(String fileName,Long fileSize, byte[] content) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.content = content;
    }
}
