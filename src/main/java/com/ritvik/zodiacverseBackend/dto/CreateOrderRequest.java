package com.ritvik.zodiacverseBackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Minimum amount is ₹1")
    private BigDecimal amount;
}