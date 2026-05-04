package com.ritvik.zodiacverseBackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKundliRequest {

    @NotBlank(message = "Person name is required")
    private String personName;

    @NotBlank(message = "Gender is required")
    private String gender;            // MALE / FEMALE / OTHER

    @NotNull(message = "Birth date is required")
    private LocalDate birthDate;

    @NotNull(message = "Birth time is required")
    private LocalTime birthTime;

    @NotBlank(message = "Birth place is required")
    private String birthPlace;

    private Double latitude;
    private Double longitude;
    private String timezone;          // optional, defaults to Asia/Kolkata

    private String system;            // VEDIC / WESTERN / KP, defaults to VEDIC
}