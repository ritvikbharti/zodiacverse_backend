package com.ritvik.zodiacverseBackend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {

    @NotNull(message = "Astrologer ID is required")
    private UUID astrologerId;

    @NotNull(message = "Booking date is required")
    private LocalDate bookingDate;

    @NotNull(message = "Booking time is required")
    private LocalTime bookingTime;

    @NotBlank(message = "Consultation type is required")
    private String type;             // CHAT / VOICE / VIDEO

    @NotNull(message = "Duration is required")
    @Min(value = 5, message = "Minimum 5 minutes")
    @Max(value = 120, message = "Maximum 120 minutes")
    private Integer durationMinutes;

    private String notes;
}