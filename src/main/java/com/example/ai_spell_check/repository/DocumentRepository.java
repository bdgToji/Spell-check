package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOriginalFile_FileNameAndUser_Username(String fileName, String username);
    Optional<Document> findByIdAndUser_Username(Long id, String username);
}