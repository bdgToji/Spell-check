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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String originalContent;

    @Column(columnDefinition = "TEXT")
    private String correctedContent;

    private boolean isCorrect;

    @Column(length = 10)
    private String languageCode;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public TextEntry(String originalContent, String correctedContent, User user, boolean isCorrect){
        this.originalContent = originalContent;
        this.correctedContent = correctedContent;
        this.user = user;
        this.isCorrect = isCorrect;
    }
}