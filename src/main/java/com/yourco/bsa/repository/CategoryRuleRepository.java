package com.yourco.bsa.repository;

import com.yourco.bsa.model.CategoryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRuleRepository extends JpaRepository<CategoryRule, UUID> {
    boolean existsByKeyword(String keyword);
}
