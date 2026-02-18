package com.yourco.bsa.controller;

import com.yourco.bsa.model.Transaction;
import com.yourco.bsa.repository.TransactionRepository;
import com.yourco.bsa.service.categorization.CategorizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final TransactionRepository transactionRepository;
    private final CategorizationService categorizationService;

    public AnalyticsController(TransactionRepository transactionRepository,
            CategorizationService categorizationService) {
        this.transactionRepository = transactionRepository;
        this.categorizationService = categorizationService;
    }

    @PostMapping("/categorize/{docNumber}")
    public ResponseEntity<String> triggerCategorization(@PathVariable String docNumber) {
        categorizationService.categorizeTransactions(docNumber);
        return ResponseEntity.ok("Categorization started for document: " + docNumber);
    }

    @GetMapping("/spending-by-category")
    public ResponseEntity<Map<String, BigDecimal>> getSpendingByCategory(@RequestParam String docNumber) {
        List<Transaction> transactions = transactionRepository.findByDocNumber(docNumber);
        Map<String, BigDecimal> spendingByCategory = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getDebitAmount() != null && t.getCategory() != null) {
                spendingByCategory.merge(t.getCategory(), t.getDebitAmount(), BigDecimal::add);
            }
        }

        return ResponseEntity.ok(spendingByCategory);
    }
}
