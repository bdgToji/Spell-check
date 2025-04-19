package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.model.DocumentFile;
import com.example.ai_spell_check.model.Language;
import com.example.ai_spell_check.model.response.DocumentUploadResponse;
import com.example.ai_spell_check.service.DocumentService;
import com.example.ai_spell_check.service.LanguageCodeService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;
    private final LanguageCodeService languageCodeService;

    public DocumentController(DocumentService documentService, LanguageCodeService languageCodeService) {
        this.documentService = documentService;
        this.languageCodeService = languageCodeService;
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        List<Language> languageList = languageCodeService.findAll();
        model.addAttribute("languageList",languageList);
        model.addAttribute("bodyContent", "document-upload");
        return "master-template";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("documentFile") MultipartFile documentFile,
                                   @RequestParam("languageCode") String languageCode,
                                   @AuthenticationPrincipal UserDetails user,
                                   Model model) throws IOException {
        List<Language> languageList = languageCodeService.findAll();
        model.addAttribute("languageList",languageList);

        if (documentFile.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload");
            model.addAttribute("bodyContent", "document-upload");
            return "master-template";
        }

        if (documentFile.getSize() > 5 * 1024 * 1024) {
            model.addAttribute("error", "This file exceeds our file size limit of 5 MB. Please select a file smaller than 5 MB.");
            model.addAttribute("bodyContent", "document-upload");
            return "master-template";
        }

        DocumentUploadResponse response = documentService.processDocumentEntry(documentFile, user, languageCode);

        model.addAttribute("response", response);
        model.addAttribute("bodyContent", "document-upload");
        return "master-template";
    }

    @GetMapping("/download/{fileName}")
    public void downloadCorrectedFile(@PathVariable String fileName,
                                      @AuthenticationPrincipal UserDetails user,
                                      HttpServletResponse response) throws IOException {

        Optional<DocumentFile> documentFileOpt = documentService.getCorrectedFile(fileName, user);

        if (documentFileOpt.isPresent()) {
            DocumentFile documentFile = documentFileOpt.get();

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"corrected_" + fileName + "\"");
            response.setContentLength(documentFile.getContent().length);

            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(documentFile.getContent());
            outputStream.flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        }
    }
}
