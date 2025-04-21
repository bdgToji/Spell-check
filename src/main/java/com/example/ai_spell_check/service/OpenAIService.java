package com.example.ai_spell_check.service;

import com.example.ai_spell_check.model.response.DocumentUploadResponse;
import com.example.ai_spell_check.model.response.TextEntryResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class OpenAIService {

    private final ChatClient chatClient;

    public OpenAIService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public TextEntryResponse returnCorrectedText(String originalText, String languageCode) throws JsonProcessingException {
        String promptText = String.format(
                "I will send you a text and I want you to check if it contains any spelling or grammar errors.\n" +
                        "I also want you to define a title that will be of 2 or 3 words depending on the text content.\n" +
                        "The language of the text is: \"%s\".\n" +
                        "Return a JSON in this format:\n" +
                        "{\n" +
                        "  \"title\": [your defined title],\n" +
                        "  \"correct\": true,\n" +
                        "  \"correctedText\": \"same as original text\"\n" +
                        "}\n\n" +
                        "If there are errors that need correction, return:\n" +
                        "{\n" +
                        "  \"title\": [your defined title],\n" +
                        "  \"correct\": false,\n" +
                        "  \"correctedText\": \"corrected version of the text\"\n" +
                        "}\n\n" +
                        "Set \"correct\" to true ONLY if the original text has no errors whatsoever.\n\n" +
                        "Text: \"%s\"",
                languageCode, originalText
        );

        Prompt prompt = new Prompt(List.of(new UserMessage(promptText)));
        ChatResponse response = chatClient.call(prompt);
        String content = response.getResult().getOutput().getContent();

        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(content, TextEntryResponse.class);
    }

    public DocumentUploadResponse returnCorrectedDocument(MultipartFile originalFile, String languageCode) throws IOException {
        String fileContent;

        // .pdf
        if (originalFile.getContentType() != null && originalFile.getContentType().equals("application/pdf")) {
            try (PDDocument document = PDDocument.load(originalFile.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                fileContent = stripper.getText(document);
            }
        }
        // .docx
        else if (originalFile.getContentType() != null &&
                originalFile.getContentType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            try (XWPFDocument document = new XWPFDocument(originalFile.getInputStream())) {
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                fileContent = extractor.getText();
            }
        }
        // .txt and others
        else {
            fileContent = new String(originalFile.getBytes());
        }

        String promptText = String.format(
                "I will send you a text from a document and I want you to check if it contains any spelling or grammar errors.\n" +
                        "The language of the text is: \"%s\".\n" +
                        "Your task is to correct ALL spelling and grammar errors while preserving the original meaning.\n" +
                        "Return a JSON in this format:\n" +
                        "{\n" +
                        "  \"correct\": true,\n" +
                        "  \"correctedContent\": \"exact same text as the original\"\n" +
                        "}\n\n" +
                        "If there are errors that need correction, return:\n" +
                        "{\n" +
                        "  \"correct\": false,\n" +
                        "  \"correctedContent\": \"fully corrected version of the text with all spelling and grammar errors fixed\"\n" +
                        "}\n\n" +
                        "Important: Always include the full text in the correctedContent field, never just a comment about the errors.\n" +
                        "Set \"correct\" to true ONLY if the original text has no errors whatsoever.\n\n" +
                        "Document content: \"%s\"",
                languageCode, fileContent
        );


        Prompt prompt = new Prompt(List.of(new UserMessage(promptText)));
        ChatResponse response = chatClient.call(prompt);
        String content = response.getResult().getOutput().getContent();

        ObjectMapper mapper = new ObjectMapper();
        DocumentUploadResponse result = mapper.readValue(content, DocumentUploadResponse.class);
        result.setFileName(originalFile.getOriginalFilename());

        return result;
    }

}
