package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AstrologerResponse {
    private UUID id;
    private String name;
    private String bio;
    private List<String> specialties;
    private List<String> languages;
    private Integer experienceYears;
    private BigDecimal rating;
    private Integer reviewsCount;
    private BigDecimal chatRate;
    private BigDecimal voiceRate;
    private BigDecimal videoRate;
    private boolean online;
    private String avatarUrl;
}