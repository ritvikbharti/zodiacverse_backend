package com.ritvik.zodiacverseBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "horoscopes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sign", "period", "horoscope_date"}))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Horoscope {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String sign;          // "Aries", "Taurus"... etc

    @Column(nullable = false)
    private String period;        // daily, weekly, monthly, yearly

    @Column(nullable = false)
    private LocalDate horoscopeDate;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text;

    private String mood;
    private Integer luckyNumber;
    private String color;
    private String emoji;
}