package com.example.ai_spell_check.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TextEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalContent;

    @Column(columnDefinition = "TEXT")
    private String correctedContent;

    private boolean isCorrect;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    private Language languageCode;

    public TextEntry(Language languageCode, String title, String originalContent, String correctedContent, User user, boolean isCorrect){
        this.languageCode = languageCode;
        this.title = title;
        this.originalContent = originalContent;
        this.correctedContent = correctedContent;
        this.user = user;
        this.isCorrect = isCorrect;
    }
}