package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.Astrologer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AstrologerRepo extends JpaRepository<Astrologer, UUID> {

    @Query("""
        SELECT a FROM Astrologer a
        WHERE (:q IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(a.specialties) LIKE LOWER(CONCAT('%', :q, '%')))
    """)
    Page<Astrologer> search(String q, Pageable pageable);
}