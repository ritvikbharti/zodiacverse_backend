package com.ritvik.zodiacverseBackend.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private UUID id;
    private UUID astrologerId;
    private String astrologerName;
    private String astrologerAvatar;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private String type;
    private Integer durationMinutes;
    private BigDecimal amount;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
}