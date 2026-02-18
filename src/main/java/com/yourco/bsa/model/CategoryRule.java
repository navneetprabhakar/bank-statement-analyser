package com.yourco.bsa.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "category_rules")
public class CategoryRule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String keyword;

    @Column(nullable = false)
    private String category;

    @Column(name = "match_type") // CONTAINS, STARTS_WITH, EXACT
    private String matchType = "CONTAINS";

    public CategoryRule() {
    }

    public CategoryRule(String keyword, String category) {
        this.keyword = keyword;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }
}
