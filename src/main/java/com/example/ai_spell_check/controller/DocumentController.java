package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.model.DocumentFile;
import com.example.ai_spell_check.model.response.DocumentUploadResponse;
import com.example.ai_spell_check.service.DocumentService;
import com.example.ai_spell_check.service.OpenAIService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/document")
public class DocumentController {

    private final DocumentService documentService;
    private final OpenAIService openAIService;

    public DocumentController(DocumentService documentService, OpenAIService openAIService) {
        this.documentService = documentService;
        this.openAIService = openAIService;
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("bodyContent", "document-upload");
        return "master-template";
    }

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("documentFile") MultipartFile documentFile,
                                   @AuthenticationPrincipal UserDetails user,
                                   Model model) throws IOException {
        String username = user.getUsername();

        if (documentFile.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload");
            model.addAttribute("bodyContent", "document-upload");
            return "master-template";
        }

        DocumentUploadResponse response = openAIService.returnCorrectedDocument(documentFile);
        documentService.createDocumentEntry(documentFile, response, username);

        model.addAttribute("response", response);
        model.addAttribute("bodyContent", "document-upload");
        return "master-template";
    }

    @GetMapping("/download/{fileName}")
    public void downloadCorrectedFile(@PathVariable String fileName,
                                      @AuthenticationPrincipal UserDetails user,
                                      HttpServletResponse response) throws IOException {

        String username = user.getUsername();
        Optional<DocumentFile> documentFileOpt = documentService.getCorrectedFile(fileName, username);

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
