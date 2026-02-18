package com.yourco.bsa.service.template;

import com.yourco.bsa.model.Document;
import com.yourco.bsa.repository.DocumentRepository;
import com.yourco.bsa.service.ai.GeminiService;
import com.yourco.bsa.service.processing.PdfProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class TemplateService {

    private static final Logger log = LoggerFactory.getLogger(TemplateService.class);

    private final DocumentRepository documentRepository;
    private final PdfProcessor pdfProcessor;
    private final GeminiService geminiService;

    public TemplateService(DocumentRepository documentRepository, PdfProcessor pdfProcessor,
            GeminiService geminiService) {
        this.documentRepository = documentRepository;
        this.pdfProcessor = pdfProcessor;
        this.geminiService = geminiService;
    }

    @Transactional
    public void processDocument(UUID documentId) {
        log.info("Processing document ID: {}", documentId);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));

        if (!"PENDING".equals(document.getStatus())) {
            log.warn("Document {} is not in PENDING state. Skipping.", document.getDocNumber());
            return;
        }

        try {
            Path filePath = Paths.get(document.getStoredPath());

            // 1. Validate PDF
            if (!pdfProcessor.isValidPdf(filePath)) {
                updateStatus(document, "FAILED", "Invalid or encrypted PDF");
                return;
            }

            // 2. Extract Text
            String extractedText = pdfProcessor.extractText(filePath);
            int pageCount = pdfProcessor.getPageCount(filePath);

            document.setPageCount(pageCount);

            // 3. Detect Template via Gemini
            // Take first 1000 chars for classification to save tokens/cost
            String sampleText = extractedText.length() > 1000 ? extractedText.substring(0, 1000) : extractedText;
            String classification = geminiService.classifyTemplate(sampleText);

            log.info("Document classified as: {}", classification);

            // For now, we just log the classification. In real app, we would resolve this
            // to a Template ID.
            // document.setTemplateId(resolvedTemplateId);

            updateStatus(document, "PROCESSED", "Classified as: " + classification);

        } catch (IOException e) {
            log.error("Error processing document {}: {}", document.getDocNumber(), e.getMessage());
            updateStatus(document, "FAILED", "Processing error: " + e.getMessage());
        }
    }

    private void updateStatus(Document document, String status, String message) {
        document.setStatus(status);
        // In a real app, we might save the 'message' to an audit log or a 'remarks'
        // field
        log.info("Updating document {} status to {}. Message: {}", document.getDocNumber(), status, message);
        documentRepository.save(document);
    }
}
