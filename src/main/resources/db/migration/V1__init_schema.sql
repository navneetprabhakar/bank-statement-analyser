-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Master document registry
CREATE TABLE documents (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doc_number          VARCHAR(30) UNIQUE NOT NULL,
    original_filename   VARCHAR(255),
    stored_path         TEXT NOT NULL,
    file_hash_sha256    VARCHAR(64) NOT NULL,
    file_size_bytes     BIGINT,
    page_count          INT,
    upload_timestamp    TIMESTAMPTZ DEFAULT NOW(),
    uploaded_by         VARCHAR(100),
    status              VARCHAR(30),
    template_id         UUID,
    session_id          UUID
);

-- Authenticity verification results
CREATE TABLE document_verification (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doc_number          VARCHAR(30) REFERENCES documents(doc_number),
    verification_type   VARCHAR(30),
    status              VARCHAR(20),
    confidence_score    DECIMAL(5,2),
    details             JSONB,
    verified_at         TIMESTAMPTZ DEFAULT NOW()
);

-- Identified bank templates
CREATE TABLE bank_templates (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bank_name           VARCHAR(100),
    template_version    VARCHAR(30),
    country_code        CHAR(2),
    extraction_config   JSONB,
    created_at          TIMESTAMPTZ DEFAULT NOW(),
    is_llm_discovered   BOOLEAN DEFAULT FALSE
);

-- Extracted statement header
CREATE TABLE statement_headers (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doc_number          VARCHAR(30) REFERENCES documents(doc_number),
    account_holder      VARCHAR(200),
    account_number      VARCHAR(50),
    account_type        VARCHAR(50),
    bank_name           VARCHAR(100),
    branch_name         VARCHAR(100),
    ifsc_code           VARCHAR(20),
    statement_from      DATE,
    statement_to        DATE,
    opening_balance     DECIMAL(18,2),
    closing_balance     DECIMAL(18,2),
    currency            CHAR(3) DEFAULT 'INR',
    raw_extracted_data  JSONB
);

-- Transaction line items
CREATE TABLE transactions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doc_number          VARCHAR(30) REFERENCES documents(doc_number),
    txn_date            DATE,
    value_date          DATE,
    description         TEXT,
    reference_number    VARCHAR(100),
    debit_amount        DECIMAL(18,2),
    credit_amount       DECIMAL(18,2),
    balance             DECIMAL(18,2),
    txn_mode            VARCHAR(30),
    category            VARCHAR(50),
    sub_category        VARCHAR(50),
    entity_name         VARCHAR(200),
    entity_type         VARCHAR(20),
    is_circular         BOOLEAN DEFAULT FALSE,
    circular_doc_number VARCHAR(30),
    raw_description     TEXT,
    page_number         INT
);

-- Vendor / Customer master
CREATE TABLE entities (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type         VARCHAR(20),
    raw_name            VARCHAR(200),
    normalized_name     VARCHAR(200),
    first_seen          DATE,
    last_seen           DATE,
    total_credit        DECIMAL(18,2) DEFAULT 0,
    total_debit         DECIMAL(18,2) DEFAULT 0,
    txn_count           INT DEFAULT 0,
    tags                TEXT[]
);

-- Circular transaction registry
CREATE TABLE circular_transactions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    session_id          UUID,
    txn_id_source       UUID REFERENCES transactions(id),
    txn_id_target       UUID REFERENCES transactions(id),
    source_doc_number   VARCHAR(30),
    target_doc_number   VARCHAR(30),
    amount              DECIMAL(18,2),
    match_confidence    DECIMAL(5,2),
    detected_at         TIMESTAMPTZ DEFAULT NOW()
);

-- Audit log (append-only)
CREATE TABLE audit_log (
    id                  BIGSERIAL PRIMARY KEY,
    doc_number          VARCHAR(30),
    session_id          UUID,
    step_name           VARCHAR(80),
    event_type          VARCHAR(20),
    actor               VARCHAR(100),
    payload             JSONB,
    duration_ms         BIGINT,
    error_message       TEXT,
    logged_at           TIMESTAMPTZ DEFAULT NOW()
);

-- LLM Interaction log
CREATE TABLE llm_interactions (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    doc_number          VARCHAR(30),
    step_name           VARCHAR(80),
    model_name          VARCHAR(50),
    prompt_tokens       INT,
    completion_tokens   INT,
    prompt_text         TEXT,
    response_text       TEXT,
    latency_ms          BIGINT,
    created_at          TIMESTAMPTZ DEFAULT NOW()
);

-- DB-driven categorisation rules
CREATE TABLE categorisation_rules (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    rule_name       VARCHAR(100),
    pattern         TEXT NOT NULL,
    match_field     VARCHAR(30),
    category        VARCHAR(50) NOT NULL,
    sub_category    VARCHAR(50),
    entity_hint     VARCHAR(100),
    priority        INT DEFAULT 100,
    confidence      DECIMAL(4,2) DEFAULT 1.0,
    is_active       BOOLEAN DEFAULT TRUE,
    created_by      VARCHAR(100),
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);

-- Categorisation decision audit
CREATE TABLE categorisation_audit (
    id                  BIGSERIAL PRIMARY KEY,
    doc_number          VARCHAR(30) NOT NULL,
    txn_id              UUID NOT NULL REFERENCES transactions(id),
    category            VARCHAR(50),
    sub_category        VARCHAR(50),
    confidence          DECIMAL(4,2),
    decision_source     VARCHAR(20) NOT NULL,
    rule_id             UUID REFERENCES categorisation_rules(id),
    rule_name           VARCHAR(100),
    matched_pattern     TEXT,
    matched_text        TEXT,
    llm_interaction_id  UUID REFERENCES llm_interactions(id),
    llm_raw_response    JSONB,
    overridden_by       VARCHAR(100),
    previous_category   VARCHAR(50),
    previous_sub_cat    VARCHAR(50),
    override_reason     TEXT,
    was_overridden      BOOLEAN DEFAULT FALSE,
    overridden_at       TIMESTAMPTZ,
    decided_at          TIMESTAMPTZ DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_cat_audit_doc     ON categorisation_audit(doc_number);
CREATE INDEX idx_cat_audit_txn     ON categorisation_audit(txn_id);
CREATE INDEX idx_cat_audit_source  ON categorisation_audit(decision_source);
CREATE INDEX idx_cat_audit_decided ON categorisation_audit(decided_at);
