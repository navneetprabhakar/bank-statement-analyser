package com.yourco.bsa.service.processing;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PdfProcessorTest {

    private PdfProcessor pdfProcessor;

    @BeforeEach
    void setUp() {
        pdfProcessor = new PdfProcessor();
    }

    @Test
    void shouldValidateValidPdf(@TempDir Path tempDir) throws IOException {
        Path pdfPath = tempDir.resolve("valid.pdf");
        createSamplePdf(pdfPath, "Hello World");

        assertTrue(pdfProcessor.isValidPdf(pdfPath));
    }

    @Test
    void shouldExtractTextFromPdf(@TempDir Path tempDir) throws IOException {
        Path pdfPath = tempDir.resolve("extract.pdf");
        String content = "Bank Statement Transaction List";
        createSamplePdf(pdfPath, content);

        String extracted = pdfProcessor.extractText(pdfPath);
        assertTrue(extracted.contains("Bank Statement"));
        assertTrue(extracted.contains("Transaction List"));
    }

    @Test
    void shouldGetPageCount(@TempDir Path tempDir) throws IOException {
        Path pdfPath = tempDir.resolve("pages.pdf");
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage());
            doc.addPage(new PDPage());
            doc.save(pdfPath.toFile());
        }

        assertEquals(2, pdfProcessor.getPageCount(pdfPath));
    }

    private void createSamplePdf(Path path, String content) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream contents = new PDPageContentStream(doc, page)) {
                contents.beginText();
                contents.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contents.newLineAtOffset(100, 700);
                contents.showText(content);
                contents.endText();
            }

            doc.save(path.toFile());
        }
    }
}
