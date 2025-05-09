package com.example.ai_spell_check.service;

import com.example.ai_spell_check.model.DTO.CorrectionHistoryDto;
import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.enums.ItemType;
import com.example.ai_spell_check.repository.DocumentRepository;
import com.example.ai_spell_check.repository.TextEntryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Service
public class CorrectionHistoryService {

    private final DocumentRepository documentRepository;
    private final TextEntryRepository textEntryRepository;
    private final EntityManager entityManager;

    @Autowired
    public CorrectionHistoryService(
            DocumentRepository documentRepository,
            TextEntryRepository textEntryRepository,
            EntityManager entityManager) {
        this.documentRepository = documentRepository;
        this.textEntryRepository = textEntryRepository;
        this.entityManager = entityManager;
    }

    public Page<CorrectionHistoryDto> getCombinedCorrectionHistory(String userId, Pageable pageable) {
        Query countDocumentsQuery = entityManager.createQuery(
                "SELECT COUNT(d) FROM Document d WHERE d.user.id = :userId");
        countDocumentsQuery.setParameter("userId", userId);
        Long documentCount = (Long) countDocumentsQuery.getSingleResult();

        Query countTextEntriesQuery = entityManager.createQuery(
                "SELECT COUNT(t) FROM TextEntry t WHERE t.user.id = :userId");
        countTextEntriesQuery.setParameter("userId", userId);
        Long textEntryCount = (Long) countTextEntriesQuery.getSingleResult();

        long totalCount = documentCount + textEntryCount;

        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;

        List<CorrectionHistoryDto> results = new ArrayList<>();

        String sortDirection = pageable.getSort().getOrderFor("id") != null &&
                pageable.getSort().getOrderFor("id").getDirection() == Sort.Direction.ASC ? "ASC" : "DESC";

        if (offset < documentCount) {
            Query documentsQuery = entityManager.createQuery(
                    "SELECT DISTINCT d FROM Document d " +
                            "LEFT JOIN FETCH d.originalFile " +
                            "LEFT JOIN FETCH d.correctedFile " +
                            "LEFT JOIN FETCH d.language " +
                            "WHERE d.user.id = :userId " +
                            "ORDER BY d.uploadDate " + sortDirection);
            documentsQuery.setParameter("userId", userId);
            documentsQuery.setFirstResult(offset);
            documentsQuery.setMaxResults(Math.min(pageSize, (int)(documentCount - offset)));

            List<Document> documents = documentsQuery.getResultList();
            for (Document doc : documents) {
                CorrectionHistoryDto dto = new CorrectionHistoryDto();
                dto.setItemId(doc.getId());
                dto.setItemType(ItemType.DOCUMENT);
                dto.setTitle(doc.getOriginalFile() != null ? doc.getOriginalFile().getFileName() : "Untitled Document");
                dto.setDate(doc.getUploadDate());
                dto.setLanguageName(doc.getLanguage() != null ? doc.getLanguage().getName() : "Unknown");
                dto.setCorrect(doc.isCorrect());
                dto.setFileType(doc.getFileType());
                results.add(dto);
            }
        }

        int remainingItems = pageSize - results.size();
        if (remainingItems > 0 && offset <= totalCount) {
            int textEntryOffset = (int) Math.max(0, offset - documentCount);

            Query textEntriesQuery = entityManager.createQuery(
                    "SELECT t FROM TextEntry t " +
                            "LEFT JOIN FETCH t.languageCode " +
                            "WHERE t.user.id = :userId " +
                            "ORDER BY t.creationDate " + sortDirection);
            textEntriesQuery.setParameter("userId", userId);
            textEntriesQuery.setFirstResult(textEntryOffset);
            textEntriesQuery.setMaxResults(remainingItems);

            List<TextEntry> textEntries = textEntriesQuery.getResultList();
            for (TextEntry text : textEntries) {
                CorrectionHistoryDto dto = new CorrectionHistoryDto();
                dto.setItemId(text.getId());
                dto.setItemType(ItemType.TEXT_ENTRY);
                dto.setTitle(text.getTitle() != null ? text.getTitle() : "Untitled Text");
                dto.setDate(text.getCreationDate());
                dto.setLanguageName(text.getLanguageCode() != null ? text.getLanguageCode().getName() : "Unknown");
                dto.setCorrect(text.isCorrect());
                if (text.getOriginalContent() != null) {
                    String content = text.getOriginalContent();
                    dto.setPreviewContent(content.length() > 100 ?
                            content.substring(0, 97) + "..." : content);
                }
                results.add(dto);
            }
        }

        Comparator<CorrectionHistoryDto> comparator;
        if (pageable.getSort().getOrderFor("date") != null &&
                pageable.getSort().getOrderFor("date").getDirection() == Sort.Direction.ASC) {
            comparator = Comparator.comparing(CorrectionHistoryDto::getDate);
        } else {
            comparator = Comparator.comparing(CorrectionHistoryDto::getDate).reversed();
        }
        results.sort(comparator);

        return new PageImpl<>(results, pageable, totalCount);
    }
}