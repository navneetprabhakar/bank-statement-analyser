package com.yourco.bsa.controller;

import com.yourco.bsa.dto.UploadResponseDto;
import com.yourco.bsa.service.ingestion.IngestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    private final IngestionService ingestionService;

    public DocumentController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<List<UploadResponseDto>> uploadDocuments(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "sessionId", required = false) UUID sessionId) {

        if (sessionId == null) {
            sessionId = UUID.randomUUID();
        }

        List<UploadResponseDto> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            responses.add(ingestionService.ingestDocument(file, sessionId));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(responses);
    }
}
