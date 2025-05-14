package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.model.Language;
import com.example.ai_spell_check.model.response.TextEntryResponse;
import com.example.ai_spell_check.service.LanguageCodeService;
import com.example.ai_spell_check.service.OpenAIService;
import com.example.ai_spell_check.service.TextEntryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/text")
public class TextEntryController {
    private final TextEntryService textEntryService;
    private final LanguageCodeService languageCodeService;

    public TextEntryController(TextEntryService textEntryService, LanguageCodeService languageCodeService) {
        this.textEntryService = textEntryService;
        this.languageCodeService = languageCodeService;
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        List<Language> languageList = languageCodeService.findAll();

        model.addAttribute("languageList",languageList);
        model.addAttribute("bodyContent", "text-upload");

        return "master-template";
    }

    @PostMapping("/upload")
    public String handleTextInput(@RequestParam String textInput,
                                  @RequestParam String languageCode,
                                  @AuthenticationPrincipal UserDetails user,
                                  Model model) throws JsonProcessingException {
        List<Language> languageList = languageCodeService.findAll();

        if (textInput.isEmpty()) {
            model.addAttribute("error", "Please enter a text for processing");
            model.addAttribute("languageList",languageList);
            model.addAttribute("bodyContent", "text-upload");
            return "master-template";
        }

        TextEntryResponse response = textEntryService.processTextEntry(textInput,user,languageCode);

        model.addAttribute("languageList",languageList);
        model.addAttribute("response", response);
        model.addAttribute("originalText", textInput);
        model.addAttribute("bodyContent", "text-upload");

        return "master-template";
    }
}
