package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.Compatibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompatibilityRepo extends JpaRepository<Compatibility, UUID> {

    Page<Compatibility> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<Compatibility> findByIdAndUserId(UUID id, UUID userId);
}