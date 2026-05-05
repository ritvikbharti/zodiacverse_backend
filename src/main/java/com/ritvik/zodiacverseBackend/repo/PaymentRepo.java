package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByRazorpayOrderId(String orderId);

    Page<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}