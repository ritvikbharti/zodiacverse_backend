package com.ritvik.zodiacverseBackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilityRequest {

    @NotNull(message = "Kundli 1 ID is required")
    private UUID kundli1Id;

    @NotNull(message = "Kundli 2 ID is required")
    private UUID kundli2Id;
}