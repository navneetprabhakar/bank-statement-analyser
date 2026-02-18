package com.yourco.bsa.service.template;

import com.yourco.bsa.model.Document;
import com.yourco.bsa.repository.DocumentRepository;
import com.yourco.bsa.service.ai.GeminiService;
import com.yourco.bsa.service.processing.PdfProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private PdfProcessor pdfProcessor;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private TemplateService templateService;

    @Test
    void shouldProcessPendingDocumentSuccessfully() throws IOException {
        UUID docId = UUID.randomUUID();
        Document document = new Document();
        document.setId(docId);
        document.setDocNumber("DOC-123");
        document.setStatus("PENDING");
        document.setStoredPath("/tmp/test.pdf");

        when(documentRepository.findById(docId)).thenReturn(Optional.of(document));
        when(pdfProcessor.isValidPdf(any(Path.class))).thenReturn(true);
        when(pdfProcessor.extractText(any(Path.class))).thenReturn("Sample Bank Statement Text");
        when(pdfProcessor.getPageCount(any(Path.class))).thenReturn(1);
        when(geminiService.classifyTemplate(anyString())).thenReturn("Chase Bank Statement");

        templateService.processDocument(docId);

        verify(documentRepository, times(1)).save(document);
        verify(geminiService, times(1)).classifyTemplate(anyString());
    }

    @Test
    void shouldHandleInvalidPdf() {
        UUID docId = UUID.randomUUID();
        Document document = new Document();
        document.setId(docId);
        document.setDocNumber("DOC-123");
        document.setStatus("PENDING");
        document.setStoredPath("/tmp/test.pdf");

        when(documentRepository.findById(docId)).thenReturn(Optional.of(document));
        when(pdfProcessor.isValidPdf(any(Path.class))).thenReturn(false);

        templateService.processDocument(docId);

        verify(documentRepository, times(1)).save(document); // Updated status to FAILED
        verify(geminiService, never()).classifyTemplate(anyString());
    }
}
