package com.ritvik.zodiacverseBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "kundlis")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Kundli {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -------- Person details --------
    @Column(nullable = false)
    private String personName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private LocalTime birthTime;

    @Column(nullable = false)
    private String birthPlace;

    private Double latitude;
    private Double longitude;
    private String timezone;

    private String sunSign;
    private String moonSign;
    private String ascendant;
    private String nakshatra;
    private String rashi;

    @Column(columnDefinition = "TEXT")
    private String planetaryPositionsJson;

    @Column(columnDefinition = "TEXT")
    private String housesJson;

    @Column(columnDefinition = "TEXT")
    private String predictions;

    @Enumerated(EnumType.STRING)
    @Column(name = "`system`", nullable = false)
    private KundliSystem system;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (system == null) system = KundliSystem.VEDIC;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}