package com.yourco.bsa.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final ChatClient chatClient;

    public GeminiService(GoogleGenAiChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    public String analyzeDocument(String textContext, String prompt) {
        log.info("Sending request to Gemini with context length: {}", textContext.length());
        String fullPrompt = "Context:\n" + textContext + "\n\nPrompt:\n" + prompt;
        return chatClient.prompt().user(fullPrompt).call().content();
    }

    public String classifyTemplate(String textSample) {
        log.info("Classifying template for text sample length: {}", textSample.length());
        String prompt = "Identify the bank name and document type from the following text sample. Return ONLY the bank name and document type (e.g., 'Chase Bank Statement'). If unknown, return 'UNKNOWN'.\n\nText:\n"
                + textSample;
        return chatClient.prompt().user(prompt).call().content();
    }
}
