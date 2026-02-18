package com.yourco.bsa.service.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Service
public class PdfProcessor {

    private static final Logger log = LoggerFactory.getLogger(PdfProcessor.class);

    public boolean isValidPdf(Path path) {
        try (PDDocument document = Loader.loadPDF(new File(path.toUri()))) {
            return !document.isEncrypted();
        } catch (IOException e) {
            log.error("Failed to load PDF: {}", path, e);
            return false;
        }
    }

    public String extractText(Path path) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(path.toUri()))) {
            if (document.isEncrypted()) {
                throw new IOException("PDF is encrypted and cannot be processed");
            }
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public int getPageCount(Path path) throws IOException {
        try (PDDocument document = Loader.loadPDF(new File(path.toUri()))) {
            return document.getNumberOfPages();
        }
    }
}
