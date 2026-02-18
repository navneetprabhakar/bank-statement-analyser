package com.yourco.bsa.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "doc_number")
    private String docNumber;

    @Column(name = "txn_date")
    private LocalDate txnDate;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "debit_amount")
    private BigDecimal debitAmount;

    @Column(name = "credit_amount")
    private BigDecimal creditAmount;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "txn_mode")
    private String txnMode;

    @Column(name = "category")
    private String category;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "is_circular")
    private Boolean isCircular = false;

    @Column(name = "circular_doc_number")
    private String circularDocNumber;

    @Column(name = "raw_description", columnDefinition = "TEXT")
    private String rawDescription;

    @Column(name = "page_number")
    private Integer pageNumber;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public LocalDate getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(LocalDate txnDate) {
        this.txnDate = txnDate;
    }

    public LocalDate getValueDate() {
        return valueDate;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getTxnMode() {
        return txnMode;
    }

    public void setTxnMode(String txnMode) {
        this.txnMode = txnMode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Boolean getIsCircular() {
        return isCircular;
    }

    public void setIsCircular(Boolean circular) {
        isCircular = circular;
    }

    public String getCircularDocNumber() {
        return circularDocNumber;
    }

    public void setCircularDocNumber(String circularDocNumber) {
        this.circularDocNumber = circularDocNumber;
    }

    public String getRawDescription() {
        return rawDescription;
    }

    public void setRawDescription(String rawDescription) {
        this.rawDescription = rawDescription;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
