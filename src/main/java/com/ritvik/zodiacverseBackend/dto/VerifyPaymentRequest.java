package com.ritvik.zodiacverseBackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPaymentRequest {

    @NotBlank(message = "Order ID is required")
    private String razorpayOrderId;

    @NotBlank(message = "Payment ID is required")
    private String razorpayPaymentId;

    @NotBlank(message = "Signature is required")
    private String razorpaySignature;
}