package com.yourco.bsa.service.ingestion;

import com.yourco.bsa.dto.UploadResponseDto;
import com.yourco.bsa.exception.DuplicateDocumentException;
import com.yourco.bsa.exception.InvalidFileTypeException;
import com.yourco.bsa.model.Document;
import com.yourco.bsa.repository.DocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private final DocumentRepository documentRepository;

    @Value("${bsa.storage.upload-dir:./uploads}")
    private String uploadDir;

    public IngestionService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Transactional
    public UploadResponseDto ingestDocument(MultipartFile file, UUID sessionId) {
        log.info("Starting ingestion for file: {}", file.getOriginalFilename());

        // 1. Validate MIME type
        if (!"application/pdf".equals(file.getContentType())) {
            throw new InvalidFileTypeException("Only PDF files are allowed. Received: " + file.getContentType());
        }

        try {
            // 2. Compute SHA-256 hash
            String fileHash = computeSha256(file.getBytes());

            // 3. Check for duplicates
            if (documentRepository.existsByFileHashSha256(fileHash)) {
                log.warn("Duplicate document detected with hash: {}", fileHash);
                throw new DuplicateDocumentException("Document with hash " + fileHash + " already exists.");
            }

            // 4. Generate Doc Number
            String docNumber = generateDocNumber();

            // 5. Save file to disk
            Path storedPath = saveFileToDisk(file, docNumber);

            // 6. Save Metadata to DB
            Document document = new Document();
            document.setDocNumber(docNumber);
            document.setOriginalFilename(file.getOriginalFilename());
            document.setStoredPath(storedPath.toString());
            document.setFileHashSha256(fileHash);
            document.setFileSizeBytes(file.getSize());
            document.setStatus("PENDING");
            document.setSessionId(sessionId);

            // Initial page count 0, will be updated by PDFBox later
            document.setPageCount(0);

            documentRepository.save(document);

            log.info("Document ingested successfully: {}", docNumber);

            return UploadResponseDto.builder()
                    .docNumber(docNumber)
                    .status("PENDING")
                    .originalFilename(file.getOriginalFilename())
                    .message("Upload successful")
                    .build();

        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("Error processing file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to process file upload", e);
        }
    }

    private String computeSha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String generateDocNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "DOC-" + datePart + "-" + randomPart;
    }

    private Path saveFileToDisk(MultipartFile file, String docNumber) throws IOException {
        LocalDate now = LocalDate.now();
        Path targetDir = Paths.get(uploadDir, String.valueOf(now.getYear()),
                String.format("%02d", now.getMonthValue()));

        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        Path targetPath = targetDir.resolve(docNumber + ".pdf");
        Files.write(targetPath, file.getBytes());
        return targetPath;
    }
}
