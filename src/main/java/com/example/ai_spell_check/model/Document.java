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
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String originalContent;

    @Column(length = 10)
    private String languageCode;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadDate;

    @Column(columnDefinition = "TEXT")
    private String correctedContent;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentFile correctedFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Document(String fileName, String originalContent, String languageCode){
        this.fileName = fileName;
        this.languageCode = languageCode;
        this.originalContent = originalContent;
    }
}