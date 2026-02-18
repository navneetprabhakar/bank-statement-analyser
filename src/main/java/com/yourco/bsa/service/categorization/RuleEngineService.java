package com.yourco.bsa.service.categorization;

import com.yourco.bsa.model.CategoryRule;
import com.yourco.bsa.model.Transaction;
import com.yourco.bsa.repository.CategoryRuleRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class RuleEngineService {

    private static final Logger log = LoggerFactory.getLogger(RuleEngineService.class);

    private final CategoryRuleRepository ruleRepository;

    // Cache rules in memory for performance.
    // In a distributed system, use Redis or a proper cache manager.
    private final List<CategoryRule> cachedRules = new CopyOnWriteArrayList<>();

    public RuleEngineService(CategoryRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @PostConstruct
    public void loadRules() {
        log.info("Loading category rules into memory...");
        cachedRules.clear();
        cachedRules.addAll(ruleRepository.findAll());
        log.info("Loaded {} rules.", cachedRules.size());
    }

    public Optional<String> categorize(Transaction transaction) {
        if (transaction.getDescription() == null) {
            return Optional.empty();
        }

        String desc = transaction.getDescription().toUpperCase();

        for (CategoryRule rule : cachedRules) {
            String keyword = rule.getKeyword().toUpperCase();
            if (desc.contains(keyword)) {
                log.debug("Match found: {} -> {}", desc, rule.getCategory());
                return Optional.of(rule.getCategory());
            }
        }

        return Optional.empty();
    }

    public void reloadRules() {
        loadRules();
    }
}
