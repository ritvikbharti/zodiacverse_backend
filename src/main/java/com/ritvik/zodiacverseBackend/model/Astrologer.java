package com.ritvik.zodiacverseBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "astrologers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Astrologer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false)
    private String specialties;       // comma-separated: "Vedic,Marriage,Career"

    @Column(nullable = false)
    private String languages;         // comma-separated: "Hindi,English,Marathi"

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating;        // 0.00 - 5.00

    @Column(nullable = false)
    private Integer reviewsCount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal chatRate;      // ₹/min

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal voiceRate;     // ₹/min

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal videoRate;     // ₹/min

    @Column(nullable = false)
    private boolean online;

    private String avatarUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        if (rating == null) rating = BigDecimal.ZERO;
        if (reviewsCount == null) reviewsCount = 0;
    }
}