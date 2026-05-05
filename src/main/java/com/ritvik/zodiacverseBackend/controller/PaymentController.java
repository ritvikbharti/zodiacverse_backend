package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.*;
import com.ritvik.zodiacverseBackend.service.PaymentService;
import com.ritvik.zodiacverseBackend.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        String email = authentication.getName();
        CreateOrderResponse response = paymentService.createOrder(email, request);

        return ResponseEntity.ok(
                ApiResponse.<CreateOrderResponse>builder()
                        .message("Order created successfully")
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<PaymentResponse>> verify(
            Authentication authentication,
            @Valid @RequestBody VerifyPaymentRequest request
    ) {
        String email = authentication.getName();
        PaymentResponse response = paymentService.verifyAndCredit(email, request);

        return ResponseEntity.ok(
                ApiResponse.<PaymentResponse>builder()
                        .message("Payment verified and wallet credited!")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Map<String, Object>>> history(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String email = authentication.getName();
        Page<PaymentResponse> result = paymentService.history(email, page, size);

        Map<String, Object> data = new HashMap<>();
        data.put("payments", result.getContent());
        data.put("totalItems", result.getTotalElements());
        data.put("totalPages", result.getTotalPages());

        return ResponseEntity.ok(
                ApiResponse.<Map<String, Object>>builder()
                        .message("Payment history fetched")
                        .data(data)
                        .build()
        );
    }
}