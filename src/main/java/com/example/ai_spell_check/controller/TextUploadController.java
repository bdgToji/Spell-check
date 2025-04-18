package com.example.ai_spell_check.controller;

import com.example.ai_spell_check.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TextUploadController {

    @Autowired
    private OpenAIService openAIService;

    // Show the page to input text
    @GetMapping("/upload")
    public String uploadPage(Model model) {
        model.addAttribute("bodyContent", "upload");  // Set the body content to "upload"
        return "master-template";  // Return the master-template, which includes the header and footer
    }

    // Handle form submission for grammar check
    @PostMapping("/upload")
    public String handleTextInput(String textInput, Model model) {
        // Process the submitted text using OpenAI API
        String correctedText = openAIService.fixGrammar(textInput);

        // Add the original and corrected text to the model
        model.addAttribute("originalText", textInput);
        model.addAttribute("correctedText", correctedText);
        model.addAttribute("bodyContent", "upload");  // Ensure body content is set to "upload"

        return "master-template";  // Return the master-template, including the header and footer
    }
}
