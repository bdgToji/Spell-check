package com.example.ai_spell_check.service;

import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.DocumentFile;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.model.response.DocumentUploadResponse;
import com.example.ai_spell_check.repository.DocumentRepository;
import com.example.ai_spell_check.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private  final UserRepository userRepository;

    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public void createDocumentEntry(MultipartFile original, DocumentUploadResponse response, String username) throws IOException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        DocumentFile originalFile = new DocumentFile(
                original.getOriginalFilename(),
                original.getSize(),
                original.getBytes()
        );

        // Generate PDF from corrected text
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (PDDocument pdfDocument = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            pdfDocument.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.setLeading(14.5f);
            contentStream.newLineAtOffset(50, 700);

            String[] lines = response.getCorrectedContent().split("\n");
            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLine();
            }

            contentStream.endText();
            contentStream.close();

            pdfDocument.save(outputStream);
        }

        byte[] correctedPdfBytes = outputStream.toByteArray();

        DocumentFile correctedFile = new DocumentFile(
                "corrected_" + original.getOriginalFilename(),
                (long) correctedPdfBytes.length,
                correctedPdfBytes
        );


        Document document = new Document(originalFile, correctedFile, user.get(), response.isCorrect());

        this.documentRepository.save(document);
    }

    @Transactional
    public Optional<DocumentFile> getCorrectedFile(String fileName, String username) {
        List<Document> documents = documentRepository.findByOriginalFile_FileNameAndUser_Username(fileName, username);

        return documents.stream()
                .sorted(Comparator.comparing(Document::getUploadDate).reversed())
                .map(Document::getCorrectedFile)
                .findFirst();
    }

}
