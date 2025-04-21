package com.example.ai_spell_check.service;

import com.example.ai_spell_check.model.*;
import com.example.ai_spell_check.model.enums.FileType;
import com.example.ai_spell_check.model.enums.ItemType;
import com.example.ai_spell_check.model.response.DocumentUploadResponse;
import com.example.ai_spell_check.repository.CorrectionHistoryRepository;
import com.example.ai_spell_check.repository.DocumentRepository;
import com.example.ai_spell_check.repository.LanguageCodeRepository;
import com.example.ai_spell_check.repository.UserRepository;
import com.example.ai_spell_check.utils.UtilityClass;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final LanguageCodeRepository languageCodeRepository;
    private final CorrectionHistoryRepository correctionHistoryRepository;

    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository,
                           OpenAIService openAIService, LanguageCodeRepository languageCodeRepository,
                           CorrectionHistoryRepository correctionHistoryRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.openAIService = openAIService;
        this.languageCodeRepository = languageCodeRepository;
        this.correctionHistoryRepository = correctionHistoryRepository;
    }

    @Transactional
    public DocumentUploadResponse processDocumentEntry(MultipartFile documentFile, UserDetails user,
                                                       String languageCode) throws IOException {
        DocumentUploadResponse response = openAIService.returnCorrectedDocument(documentFile, languageCode);
        Optional<User> userOpt = userRepository.findByUsername(user.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        DocumentFile originalFile = new DocumentFile(
                documentFile.getOriginalFilename(),
                documentFile.getSize(),
                documentFile.getBytes()
        );

        FileType fileType = determineFileTypeFromName(documentFile.getOriginalFilename());

        byte[] correctedFileBytes;
        String correctedFileName = "corrected_" + documentFile.getOriginalFilename();

        correctedFileBytes = switch (fileType) {
            case PDF -> UtilityClass.generatePdfFromText(response.getCorrectedContent());
            case DOCX -> UtilityClass.generateDocxFromText(response.getCorrectedContent());
            case TXT -> UtilityClass.generateTxtFromText(response.getCorrectedContent());
        };

        DocumentFile correctedFile = new DocumentFile(
                correctedFileName,
                (long) correctedFileBytes.length,
                correctedFileBytes
        );

        Language language = languageCodeRepository.findByCode(languageCode);

        Document document = new Document(language, originalFile, correctedFile, userOpt.get(), response.isCorrect());
        document.setFileType(fileType);
        this.documentRepository.save(document);

        CorrectionHistory correctionHistory = new CorrectionHistory(userOpt.get(), document.getId(), ItemType.DOCUMENT);
        correctionHistoryRepository.save(correctionHistory);

        return response;
    }

    @Transactional
    public Optional<DocumentFile> getCorrectedFile(String fileName, UserDetails user) {
        String username = user.getUsername();
        List<Document> documents = documentRepository.findByOriginalFile_FileNameAndUser_Username(fileName, username);
        return documents.stream()
                .sorted(Comparator.comparing(Document::getUploadDate).reversed())
                .map(Document::getCorrectedFile)
                .findFirst();
    }

    @Transactional
    public Optional<DocumentFile> getCorrectedFileById(Long documentId, UserDetails user) {
        String username = user.getUsername();
        Optional<Document> document = documentRepository.findByIdAndUser_Username(documentId, username);
        return document.map(Document::getCorrectedFile);
    }

    private FileType determineFileTypeFromName(String fileName) {
        if (fileName == null) {
            return FileType.PDF;
        }

        String lowerCaseFileName = fileName.toLowerCase();
        if (lowerCaseFileName.endsWith(".docx")) {
            return FileType.DOCX;
        } else if (lowerCaseFileName.endsWith(".txt")) {
            return FileType.TXT;
        } else if (lowerCaseFileName.endsWith(".pdf")) {
            return FileType.PDF;
        } else {
            return FileType.PDF;
        }
    }
}