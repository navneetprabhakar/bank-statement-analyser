package com.yourco.bsa.service.categorization;

import com.yourco.bsa.model.Transaction;
import com.yourco.bsa.repository.TransactionRepository;
import com.yourco.bsa.service.ai.GeminiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategorizationServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private RuleEngineService ruleEngineService;

    @Mock
    private GeminiService geminiService;

    @InjectMocks
    private CategorizationService categorizationService;

    @Test
    void shouldCategorizeUsingRuleEngine() {
        Transaction t1 = new Transaction();
        t1.setDescription("STARBUCKS");

        when(transactionRepository.findByDocNumber("DOC-123")).thenReturn(List.of(t1));
        when(ruleEngineService.categorize(t1)).thenReturn(Optional.of("Food & Drink"));

        categorizationService.categorizeTransactions("DOC-123");

        verify(ruleEngineService, times(1)).categorize(t1);
        verify(geminiService, never()).analyzeDocument(anyString(), anyString());
        verify(transactionRepository, times(1)).save(t1);
    }

    @Test
    void shouldFallbackToGemini() {
        Transaction t1 = new Transaction();
        t1.setDescription("UNKNOWN STORE");

        when(transactionRepository.findByDocNumber("DOC-123")).thenReturn(List.of(t1));
        when(ruleEngineService.categorize(t1)).thenReturn(Optional.empty());
        when(geminiService.analyzeDocument(anyString(), anyString())).thenReturn("Shopping");

        categorizationService.categorizeTransactions("DOC-123");

        verify(ruleEngineService, times(1)).categorize(t1);
        verify(geminiService, times(1)).analyzeDocument(eq("UNKNOWN STORE"), anyString());
        verify(transactionRepository, times(1)).save(t1);
    }
}
