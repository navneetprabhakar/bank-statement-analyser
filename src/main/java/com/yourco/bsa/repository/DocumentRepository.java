package com.yourco.bsa.repository;

import com.yourco.bsa.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findByDocNumber(String docNumber);

    boolean existsByFileHashSha256(String fileHashSha256);
}
