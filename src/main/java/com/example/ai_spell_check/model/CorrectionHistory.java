package com.example.ai_spell_check.model;

import com.example.ai_spell_check.model.enums.ItemType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "correction_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long itemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType itemType;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    CorrectionHistory(User user, Long itemId, ItemType itemType){
        this.user = user;
        this.itemId = itemId;
        this.itemType = itemType;
    }
}