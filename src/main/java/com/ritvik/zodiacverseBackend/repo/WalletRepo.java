package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepo extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUserId(UUID userId);
}