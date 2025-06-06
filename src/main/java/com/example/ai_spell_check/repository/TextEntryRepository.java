package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextEntryRepository extends JpaSpecificationRepository<TextEntry, Long> {
  Page<TextEntry> findByUser(User user, Pageable pageable);

  @Query("SELECT t FROM TextEntry t " +
          "LEFT JOIN FETCH t.languageCode " +
          "WHERE t.user = :user " +
          "ORDER BY t.creationDate DESC")
  List<TextEntry> findRecentByUser(@Param("user") User user, Pageable pageable);

}
