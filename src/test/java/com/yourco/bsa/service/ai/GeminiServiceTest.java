package com.yourco.bsa.service.ai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class GeminiServiceTest {

    @Autowired
    private GeminiService geminiService;

    @MockBean
    private GoogleGenAiChatModel chatModel;

    @Test
    void shouldAnalyzeDocument() {
        String context = "Transaction: $50.00 at Walmart";
        String prompt = "Extract amount";
        String expectedResponse = "$50.00";

        // Mock the ChatResponse
        ChatResponse response = new ChatResponse(List.of(new Generation(new AssistantMessage(expectedResponse))));
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        String result = geminiService.analyzeDocument(context, prompt);

        assertEquals(expectedResponse, result);
    }

    @Test
    void shouldClassifyTemplate() {
        String sample = "Welcome to Chase Bank";
        String expectedResponse = "Chase Bank Statement";

        ChatResponse response = new ChatResponse(List.of(new Generation(new AssistantMessage(expectedResponse))));
        when(chatModel.call(any(Prompt.class))).thenReturn(response);

        String result = geminiService.classifyTemplate(sample);

        assertEquals(expectedResponse, result);
    }
}
