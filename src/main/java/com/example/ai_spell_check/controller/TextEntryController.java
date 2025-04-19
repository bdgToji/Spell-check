package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.model.response.TextEntryResponse;
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

@Controller
@RequestMapping("/text")
public class TextEntryController {
    private final TextEntryService textEntryService;
    private final OpenAIService openAIService;

    public TextEntryController(TextEntryService textEntryService, OpenAIService openAIService) {
        this.textEntryService = textEntryService;
        this.openAIService = openAIService;
    }

    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("bodyContent", "text-upload");
        return "master-template";
    }

    @PostMapping("/upload")
    public String handleTextInput(String textInput, @AuthenticationPrincipal UserDetails user, Model model) throws JsonProcessingException {
        TextEntryResponse response = openAIService.returnCorrectedText(textInput);
        textEntryService.createTextEntry(user.getUsername(),textInput,response.getCorrectedText(), response.isCorrect());
        model.addAttribute("response", response);
        model.addAttribute("originalText", textInput);
        model.addAttribute("bodyContent", "text-upload");
        return "master-template";
    }
}
