package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaSpecificationRepository<Document, Long> {
    List<Document> findByOriginalFile_FileNameAndUser_Username(String fileName, String username);
    Optional<Document> findByIdAndUser_Username(Long id, String username);

    @EntityGraph(attributePaths = {"originalFile", "correctedFile", "language"})
    Page<Document> findByUser(User user, Pageable pageable);

    @Query("SELECT d FROM Document d JOIN FETCH d.originalFile of JOIN FETCH d.correctedFile cf " +
            "WHERE d.user.id = :userId " +
            "ORDER BY d.uploadDate DESC")
    List<Document> findDocumentMetadataByUserId(@Param("userId") Long userId);

    @Query("SELECT d FROM Document d " +
            "LEFT JOIN FETCH d.originalFile " +
            "LEFT JOIN FETCH d.correctedFile " +
            "LEFT JOIN FETCH d.language " +
            "WHERE d.user = :user AND d.id IS NOT NULL " +
            "ORDER BY d.uploadDate DESC")
    List<Document> findRecentByUser(@Param("user") User user, Pageable pageable);
}