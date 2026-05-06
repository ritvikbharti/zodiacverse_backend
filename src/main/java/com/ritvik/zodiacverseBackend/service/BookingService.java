package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.BookingResponse;
import com.ritvik.zodiacverseBackend.dto.CreateBookingRequest;
import com.ritvik.zodiacverseBackend.dto.WalletActionRequest;
import com.ritvik.zodiacverseBackend.model.*;
import com.ritvik.zodiacverseBackend.repo.AstrologerRepo;
import com.ritvik.zodiacverseBackend.repo.BookingRepo;
import com.ritvik.zodiacverseBackend.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepo bookingRepo;
    private final AstrologerRepo astrologerRepo;
    private final UserRepo userRepo;
    private final WalletService walletService;
    private final NotificationService notificationService; //  ADD

    @Transactional
    public BookingResponse create(String email, CreateBookingRequest req) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Astrologer astrologer = astrologerRepo.findById(req.getAstrologerId())
                .orElseThrow(() -> new RuntimeException("Astrologer not found"));

        ConsultationType type;
        try {
            type = ConsultationType.valueOf(req.getType().toUpperCase());
        } catch (Exception e) {
            throw new RuntimeException("Invalid consultation type. Use CHAT, VOICE, or VIDEO");
        }

        // Calculate amount
        BigDecimal rate = switch (type) {
            case CHAT  -> astrologer.getChatRate();
            case VOICE -> astrologer.getVoiceRate();
            case VIDEO -> astrologer.getVideoRate();
        };
        BigDecimal amount = rate.multiply(BigDecimal.valueOf(req.getDurationMinutes()));

        // Auto-deduct from wallet
        walletService.withdraw(email,
                WalletActionRequest.builder()
                        .amount(amount)
                        .description(String.format("Booking with %s (%s, %d min)",
                                astrologer.getName(), type.name(), req.getDurationMinutes()))
                        .build()
        );

        Booking booking = Booking.builder()
                .user(user)
                .astrologer(astrologer)
                .bookingDate(req.getBookingDate())
                .bookingTime(req.getBookingTime())
                .type(type)
                .durationMinutes(req.getDurationMinutes())
                .amount(amount)
                .status(BookingStatus.CONFIRMED)
                .notes(req.getNotes())
                .build();

        Booking saved = bookingRepo.save(booking);

        // Notify user — booking confirmed
        // Format: "15 Jun 2025 at 10:30"
        String sessionTime = req.getBookingDate() + " at " + req.getBookingTime();
        notificationService.notifyBookingConfirmed(user, astrologer.getName(), sessionTime);

        return toDto(saved);
    }

    public Page<BookingResponse> list(String email, int page, int size) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(
                user.getId(), PageRequest.of(page, size)
        ).map(this::toDto);
    }

    public BookingResponse get(String email, UUID bookingId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Booking b = bookingRepo.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return toDto(b);
    }

    @Transactional
    public BookingResponse cancel(String email, UUID bookingId) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking b = bookingRepo.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (b.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }
        if (b.getStatus() == BookingStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed booking");
        }

        // Refund to wallet
        walletService.addMoney(email,
                WalletActionRequest.builder()
                        .amount(b.getAmount())
                        .description(String.format("Refund for cancelled booking #%s",
                                b.getId().toString().substring(0, 8)))
                        .build()
        );

        b.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(b);

        //  Notify user — booking cancelled
        notificationService.notifyBookingCancelled(
                user, b.getAstrologer().getName(), b.getAmount());

        return toDto(b);
    }

    public long count(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepo.countByUserId(user.getId());
    }

    // ── mapper ────────────────────────────────────────────────
    private BookingResponse toDto(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .astrologerId(b.getAstrologer().getId())
                .astrologerName(b.getAstrologer().getName())
                .astrologerAvatar(b.getAstrologer().getAvatarUrl())
                .bookingDate(b.getBookingDate())
                .bookingTime(b.getBookingTime())
                .type(b.getType().name())
                .durationMinutes(b.getDurationMinutes())
                .amount(b.getAmount())
                .status(b.getStatus().name())
                .notes(b.getNotes())
                .createdAt(b.getCreatedAt())
                .build();
    }
}