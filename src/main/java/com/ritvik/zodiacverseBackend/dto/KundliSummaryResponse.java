package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KundliSummaryResponse {
    private UUID id;
    private String personName;
    private LocalDate birthDate;
    private String birthPlace;
    private String sunSign;
    private String moonSign;
    private String ascendant;
    private String system;
    private LocalDateTime createdAt;
}