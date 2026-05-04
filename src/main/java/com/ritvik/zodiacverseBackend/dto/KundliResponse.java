package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KundliResponse {
    private UUID id;
    private String personName;
    private String gender;
    private LocalDate birthDate;
    private LocalTime birthTime;
    private String birthPlace;
    private Double latitude;
    private Double longitude;
    private String timezone;
    private String system;

    private String sunSign;
    private String moonSign;
    private String ascendant;
    private String nakshatra;
    private String rashi;

    private List<Map<String, Object>> planetaryPositions;
    private List<Map<String, Object>> houses;
    private String predictions;

    private LocalDateTime createdAt;
}