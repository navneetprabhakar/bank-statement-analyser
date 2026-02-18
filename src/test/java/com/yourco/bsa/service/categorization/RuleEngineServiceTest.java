package com.yourco.bsa.service.categorization;

import com.yourco.bsa.model.CategoryRule;
import com.yourco.bsa.model.Transaction;
import com.yourco.bsa.repository.CategoryRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RuleEngineServiceTest {

    @Mock
    private CategoryRuleRepository repository;

    private RuleEngineService ruleEngineService;

    @BeforeEach
    void setUp() {
        ruleEngineService = new RuleEngineService(repository);
    }

    @Test
    void shouldCategorizeTransaction() {
        when(repository.findAll()).thenReturn(List.of(
                new CategoryRule("STARBUCKS", "Food & Drink"),
                new CategoryRule("UBER", "Transport")));

        ruleEngineService.loadRules();

        Transaction t1 = new Transaction();
        t1.setDescription("STARBUCKS COFFEE");

        Optional<String> category = ruleEngineService.categorize(t1);

        assertTrue(category.isPresent());
        assertEquals("Food & Drink", category.get());
    }

    @Test
    void shouldReturnEmptyForUnknown() {
        when(repository.findAll()).thenReturn(List.of());
        ruleEngineService.loadRules();

        Transaction t = new Transaction();
        t.setDescription("UNKNOWN MERCHANT");

        Optional<String> category = ruleEngineService.categorize(t);

        assertTrue(category.isEmpty());
    }
}
