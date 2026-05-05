package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.CreateReportRequest;
import com.ritvik.zodiacverseBackend.dto.ReportResponse;
import com.ritvik.zodiacverseBackend.dto.WalletActionRequest;
import com.ritvik.zodiacverseBackend.model.*;
import com.ritvik.zodiacverseBackend.repo.KundliRepo;
import com.ritvik.zodiacverseBackend.repo.ReportRepo;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepo reportRepo;
    private final KundliRepo kundliRepo;
    private final UserRepo userRepo;
    private final WalletService walletService;
    private final ReportContentGenerator contentGenerator;

    // Report prices
    private static final Map<ReportType, BigDecimal> PRICES = Map.of(
            ReportType.CAREER,   new BigDecimal("99"),
            ReportType.LOVE,     new BigDecimal("99"),
            ReportType.HEALTH,   new BigDecimal("99"),
            ReportType.FINANCE,  new BigDecimal("99"),
            ReportType.COMPLETE, new BigDecimal("249")
    );

    private static final Map<ReportType, Integer> PAGES = Map.of(
            ReportType.CAREER, 4, ReportType.LOVE, 4,
            ReportType.HEALTH, 4, ReportType.FINANCE, 4,
            ReportType.COMPLETE, 12
    );

    @Transactional
    public ReportResponse generate(String email, CreateReportRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Kundli kundli = kundliRepo.findByIdAndUserId(req.getKundliId(), user.getId())
                .orElseThrow(() -> new RuntimeException("Kundli not found"));

        ReportType type;
        try {
            type = ReportType.valueOf(req.getType().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid report type. Use CAREER, LOVE, HEALTH, FINANCE, or COMPLETE");
        }

        BigDecimal price = PRICES.get(type);

        // Deduct from wallet
        walletService.withdraw(email, WalletActionRequest.builder()
                .amount(price)
                .description(type.name().charAt(0) + type.name().substring(1).toLowerCase()
                        + " Report — " + kundli.getPersonName())
                .build());

        // Generate content
        String content = contentGenerator.generate(kundli, type);

        String title = type.name().charAt(0) + type.name().substring(1).toLowerCase()
                + " Report — " + kundli.getPersonName();

        Report report = Report.builder()
                .user(user).kundli(kundli)
                .type(type).title(title)
                .content(content)
                .status(ReportStatus.READY)
                .price(price)
                .pages(PAGES.get(type))
                .build();

        reportRepo.save(report);
        return toDto(report);
    }

    public Page<ReportResponse> list(String email, int page, int size) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reportRepo.findByUserIdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size))
                .map(this::toDto);
    }

    public Report getEntity(String email, UUID id) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reportRepo.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Transactional
    public void delete(String email, UUID id) {
        Report r = getEntity(email, id);
        reportRepo.delete(r);
    }

    public long count(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return reportRepo.countByUserId(user.getId());
    }

    private ReportResponse toDto(Report r) {
        return ReportResponse.builder()
                .id(r.getId())
                .kundliId(r.getKundli().getId())
                .kundliPersonName(r.getKundli().getPersonName())
                .type(r.getType().name())
                .title(r.getTitle())
                .status(r.getStatus().name())
                .price(r.getPrice())
                .pages(r.getPages())
                .createdAt(r.getCreatedAt())
                .build();
    }
}