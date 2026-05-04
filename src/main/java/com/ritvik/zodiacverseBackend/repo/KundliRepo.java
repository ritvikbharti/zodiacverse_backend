package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.Kundli;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KundliRepo extends JpaRepository<Kundli, UUID> {

    Page<Kundli> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<Kundli> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);
}