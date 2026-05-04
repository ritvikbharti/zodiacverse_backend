package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.TransactionResponse;
import com.ritvik.zodiacverseBackend.dto.WalletActionRequest;
import com.ritvik.zodiacverseBackend.dto.WalletResponse;
import com.ritvik.zodiacverseBackend.model.Transaction;
import com.ritvik.zodiacverseBackend.model.Wallet;
import com.ritvik.zodiacverseBackend.service.WalletService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(Authentication authentication) {
        String email = authentication.getName();
        Wallet wallet = walletService.getOrCreateWallet(email);

        WalletResponse response = WalletResponse.builder()
                .id(wallet.getId())
                .balance(wallet.getBalance())
                .build();

        return ResponseEntity.ok(
                ApiResponse.<WalletResponse>builder()
                        .message("Wallet fetched successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<TransactionResponse>> addMoney(
            Authentication authentication,
            @Valid @RequestBody WalletActionRequest request
    ) {
        String email = authentication.getName();
        Transaction txn = walletService.addMoney(email, request);

        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .message("Money added successfully")
                        .data(toDto(txn))
                        .build()
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            Authentication authentication,
            @Valid @RequestBody WalletActionRequest request
    ) {
        String email = authentication.getName();
        Transaction txn = walletService.withdraw(email, request);

        return ResponseEntity.ok(
                ApiResponse.<TransactionResponse>builder()
                        .message("Withdrawal successful")
                        .data(toDto(txn))
                        .build()
        );
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTransactions(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = authentication.getName();
        Page<Transaction> txnPage = walletService.getTransactions(email, page, size);

        List<TransactionResponse> txns = txnPage.getContent()
                .stream()
                .map(this::toDto)
                .toList();

        Map<String, Object> data = new HashMap<>();
        data.put("transactions", txns);
        data.put("currentPage", txnPage.getNumber());
        data.put("totalPages", txnPage.getTotalPages());
        data.put("totalItems", txnPage.getTotalElements());
        data.put("pageSize", txnPage.getSize());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .message("Transactions fetched successfully")
                        .data(data)
                        .build()
        );
    }

    private TransactionResponse toDto(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .type(t.getType().name())
                .amount(t.getAmount())
                .balanceAfter(t.getBalanceAfter())
                .description(t.getDescription())
                .status(t.getStatus().name())
                .createdAt(t.getCreatedAt())
                .build();
    }
}