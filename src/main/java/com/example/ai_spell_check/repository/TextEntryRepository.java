package com.example.ai_spell_check.repository;

import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextEntryRepository extends JpaSpecificationRepository<TextEntry, Long> {
  Page<TextEntry> findByUser(User user, Pageable pageable);
  List<TextEntry> findByUser(User user);

}
