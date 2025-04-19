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

//    @Column(columnDefinition = "TEXT")
//    private String originalContent;
//
//    @Column(columnDefinition = "TEXT")
//    private String correctedContent;

    private boolean isCorrect;

    @Column(length = 10)
    private String languageCode;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadDate;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentFile originalFile;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentFile correctedFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Document(DocumentFile originalFile, DocumentFile correctedFile, User user, boolean isCorrect){
        this.originalFile = originalFile;
        this.correctedFile = correctedFile;
        this.user = user;
        this.isCorrect = isCorrect;
    }
}