package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.WalletActionRequest;
import com.ritvik.zodiacverseBackend.model.*;
import com.ritvik.zodiacverseBackend.repo.TransactionRepo;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import com.ritvik.zodiacverseBackend.repo.WalletRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepo walletRepo;
    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;

    public Wallet getOrCreateWallet(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return walletRepo.findByUserId(user.getId())
                .orElseGet(() -> walletRepo.save(
                        Wallet.builder()
                                .user(user)
                                .balance(BigDecimal.ZERO)
                                .build()
                ));
    }

    @Transactional
    public Transaction addMoney(String email, WalletActionRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = getOrCreateWallet(email);

        BigDecimal newBalance = wallet.getBalance().add(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepo.save(wallet);

        Transaction txn = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .type(TransactionType.CREDIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .description(request.getDescription() != null
                        ? request.getDescription()
                        : "Money added to wallet")
                .status(TransactionStatus.SUCCESS)
                .build();

        return transactionRepo.save(txn);
    }

    @Transactional
    public Transaction withdraw(String email, WalletActionRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = getOrCreateWallet(email);

        if (wallet.getBalance().compareTo(request.getAmount()) < 0) {
            // Save a FAILED transaction for audit
            Transaction failed = Transaction.builder()
                    .user(user)
                    .wallet(wallet)
                    .type(TransactionType.DEBIT)
                    .amount(request.getAmount())
                    .balanceAfter(wallet.getBalance())
                    .description("Withdrawal failed - insufficient balance")
                    .status(TransactionStatus.FAILED)
                    .build();
            transactionRepo.save(failed);

            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal newBalance = wallet.getBalance().subtract(request.getAmount());
        wallet.setBalance(newBalance);
        walletRepo.save(wallet);

        Transaction txn = Transaction.builder()
                .user(user)
                .wallet(wallet)
                .type(TransactionType.DEBIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .description(request.getDescription() != null
                        ? request.getDescription()
                        : "Withdrawal from wallet")
                .status(TransactionStatus.SUCCESS)
                .build();

        return transactionRepo.save(txn);
    }

    public Page<Transaction> getTransactions(String email, int page, int size) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return transactionRepo.findByUserIdOrderByCreatedAtDesc(
                user.getId(),
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );
    }
}