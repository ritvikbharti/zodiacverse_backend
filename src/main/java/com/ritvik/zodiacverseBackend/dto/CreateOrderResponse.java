package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderResponse {
    private String orderId;          // Razorpay order ID
    private BigDecimal amount;       // in rupees
    private Long amountInPaise;      // Razorpay needs paise
    private String currency;
    private String keyId;            // frontend needs this
}