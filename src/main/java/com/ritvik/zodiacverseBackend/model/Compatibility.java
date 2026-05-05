package com.ritvik.zodiacverseBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "compatibilities")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Compatibility {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kundli1_id", nullable = false)
    private Kundli kundli1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kundli2_id", nullable = false)
    private Kundli kundli2;

    @Column(nullable = false)
    private Integer totalScore;       // out of 36

    @Column(columnDefinition = "TEXT")
    private String breakdownJson;     // serialized JSON of all 8 guna scores

    @Column(columnDefinition = "TEXT")
    private String verdict;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String challenges;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() { createdAt = LocalDateTime.now(); }
}