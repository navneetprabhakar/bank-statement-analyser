package com.yourco.bsa.controller;

import com.yourco.bsa.dto.UploadResponseDto;
import com.yourco.bsa.service.ingestion.IngestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(DocumentController.class)
public class DocumentControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private IngestionService ingestionService;

        @Test
        @WithMockUser
        public void shouldUploadPdfSuccessfully() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "files",
                                "test.pdf",
                                "application/pdf",
                                "dummy content".getBytes());

                UploadResponseDto response = UploadResponseDto.builder()
                                .docNumber("DOC-20240218-1234")
                                .status("PENDING")
                                .originalFilename("test.pdf")
                                .message("Upload successful")
                                .build();

                when(ingestionService.ingestDocument(any(), any())).thenReturn(response);

                mockMvc.perform(multipart("/api/v1/documents/upload")
                                .file(file)
                                .param("sessionId", UUID.randomUUID().toString())
                                .with(csrf()))
                                .andExpect(status().isAccepted())
                                .andExpect(jsonPath("$[0].docNumber").value("DOC-20240218-1234"))
                                .andExpect(jsonPath("$[0].status").value("PENDING"));
        }
}
