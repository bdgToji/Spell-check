package com.example.ai_spell_check.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UtilityClass {

    private static final float MARGIN = 50;
    private static final float FONT_SIZE = 12;
    private static final float LEADING = 14.5f;
    private static final PDType1Font FONT = PDType1Font.HELVETICA;

    public static byte[] generatePdfFromText(String text) throws IOException {
        try (PDDocument pdfDocument = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(PDRectangle.LETTER);
            pdfDocument.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
            contentStream.setFont(FONT, FONT_SIZE);
            contentStream.setLeading(LEADING);
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN, PDRectangle.LETTER.getHeight() - MARGIN);

            float width = PDRectangle.LETTER.getWidth() - 2 * MARGIN;
            String[] words = text.split("\\s+");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String testLine = line + word + " ";
                float size = FONT.getStringWidth(testLine) / 1000 * FONT_SIZE;

                if (size > width) {
                    contentStream.showText(line.toString().trim());
                    contentStream.newLine();
                    line = new StringBuilder(word + " ");
                } else {
                    line.append(word).append(" ");
                }
            }

            if (!line.isEmpty()) {
                contentStream.showText(line.toString().trim());
            }

            contentStream.endText();
            contentStream.close();

            pdfDocument.save(outputStream);
            return outputStream.toByteArray();
        }
    }
}
