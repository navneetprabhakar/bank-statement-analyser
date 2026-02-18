package com.yourco.bsa.service.categorization;

import com.yourco.bsa.model.Transaction;
import com.yourco.bsa.repository.TransactionRepository;
import com.yourco.bsa.service.ai.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorizationService {

    private static final Logger log = LoggerFactory.getLogger(CategorizationService.class);

    private final TransactionRepository transactionRepository;
    private final RuleEngineService ruleEngineService;
    private final GeminiService geminiService;

    public CategorizationService(TransactionRepository transactionRepository, RuleEngineService ruleEngineService,
            GeminiService geminiService) {
        this.transactionRepository = transactionRepository;
        this.ruleEngineService = ruleEngineService;
        this.geminiService = geminiService;
    }

    public void categorizeTransactions(String docNumber) {
        List<Transaction> transactions = transactionRepository.findByDocNumber(docNumber);
        log.info("Categorizing {} transactions for document {}", transactions.size(), docNumber);

        for (Transaction t : transactions) {
            if (t.getCategory() != null) {
                continue; // Already categorized
            }

            // 1. Rule Engine
            Optional<String> ruleCategory = ruleEngineService.categorize(t);
            if (ruleCategory.isPresent()) {
                t.setCategory(ruleCategory.get());
                t.setSubCategory("Rule Match");
            } else {
                // 2. AI Fallback
                try {
                    String aiCategory = geminiService.analyzeDocument(t.getDescription(),
                            "Categorize this transaction into a standard personal finance category (e.g., Food, Transport, Utilities). Return ONLY the category name.");
                    t.setCategory(aiCategory);
                    t.setSubCategory("AI Prediction");
                } catch (Exception e) {
                    log.error("AI Categorization failed for: {}", t.getDescription(), e);
                    t.setCategory("Uncategorized");
                }
            }
            transactionRepository.save(t);
        }
    }
}
