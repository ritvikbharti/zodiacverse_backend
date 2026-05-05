package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompatibilityResponse {
    private UUID id;
    private UUID kundli1Id;
    private String person1Name;
    private UUID kundli2Id;
    private String person2Name;

    private Integer totalScore;
    private Integer maxScore;
    private Integer percentage;
    private String verdict;
    private String verdictLevel;     // EXCELLENT / GOOD / AVERAGE / POOR

    private List<GunaScore> breakdown;

    private String strengths;
    private String challenges;
    private String recommendation;

    private LocalDateTime createdAt;
}