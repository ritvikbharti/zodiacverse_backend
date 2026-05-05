package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepo extends JpaRepository<Report, UUID> {

    Page<Report> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<Report> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);
}