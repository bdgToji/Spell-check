package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.model.Document;
import com.example.ai_spell_check.model.DocumentFile;
import com.example.ai_spell_check.model.TextEntry;
import com.example.ai_spell_check.model.User;
import com.example.ai_spell_check.service.DocumentService;
import com.example.ai_spell_check.service.TextEntryService;
import com.example.ai_spell_check.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HistoryController {
    private final UserService userService;
    private final TextEntryService textEntryService;
    private final DocumentService documentService;

    public HistoryController(UserService userService, TextEntryService textEntryService, DocumentService documentService) {
        this.userService = userService;
        this.textEntryService = textEntryService;
        this.documentService = documentService;
    }

    @GetMapping("/history")
    @Transactional(readOnly = true)
    public String getHistoryPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = this.userService.findByUsername(userDetails.getUsername());

        List<Document> allDocuments = currentUser.getDocuments();
        List<Document> validDocuments = allDocuments.stream()
                .filter(d -> d != null && d.getId() != null).toList();

        List<TextEntry> textEntries = currentUser.getTextEntries();

        List<Document> documents10 = validDocuments.stream()
                .limit(10).toList();
        List<TextEntry> textEntries10 = textEntries.stream()
                .limit(10).toList();

        model.addAttribute("textEntries", textEntries10);
        model.addAttribute("documents", documents10);
        model.addAttribute("bodyContent", "history");

        return "master-template";
    }

    @GetMapping("/history/text-entries")
    @Transactional(readOnly = true)
    public String getTextEntryHistoryPage(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        User currentUser = this.userService.findByUsername(userDetails.getUsername());
        Page<TextEntry> page = this.textEntryService.findPage(currentUser.getId(), pageNum, pageSize);

        List<TextEntry> textEntries = currentUser.getTextEntries();

        model.addAttribute("textEntries", textEntries);
        model.addAttribute("page", page);
        model.addAttribute("bodyContent", "history-text-entries");
        return "master-template";
    }

    @GetMapping("/history/documents")
    @Transactional(readOnly = true)
    public String getDocumentsHistoryPage(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize
    ) {
        User currentUser = this.userService.findByUsername(userDetails.getUsername());
        Page<Document> page = this.documentService.findPage(currentUser.getId(), pageNum, pageSize);

        List<Document> allDocuments = currentUser.getDocuments();
        List<Document> validDocuments = allDocuments.stream()
                .filter(d -> d != null && d.getId() != null).toList();


        model.addAttribute("documents", validDocuments);
        model.addAttribute("page", page);
        model.addAttribute("bodyContent", "history-documents");

        return "master-template";
    }
}