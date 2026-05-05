package com.ritvik.zodiacverseBackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kundli_id", nullable = false)
    private Kundli kundli;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;          // CAREER, LOVE, HEALTH, FINANCE, COMPLETE

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;           // full HTML content for PDF

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;      // PROCESSING, READY, FAILED

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer pages;

    private String filePath;          // saved PDF path

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = ReportStatus.PROCESSING;
    }
}