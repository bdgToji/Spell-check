package com.example.ai_spell_check.model;

import com.example.ai_spell_check.model.enums.FileType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Language language;

    @OneToOne(cascade = CascadeType.ALL)
    private DocumentFile originalFile;

    @OneToOne(cascade = CascadeType.ALL)
    private DocumentFile correctedFile;

    @ManyToOne
    private User user;

    private boolean isCorrect;

    @CreationTimestamp
    private LocalDateTime uploadDate;

    @Enumerated(EnumType.STRING)
    private FileType fileType = FileType.PDF;

    public Document(Language language, DocumentFile originalFile, DocumentFile correctedFile, User user, boolean isCorrect) {
        this.language = language;
        this.originalFile = originalFile;
        this.correctedFile = correctedFile;
        this.user = user;
        this.isCorrect = isCorrect;
    }


}