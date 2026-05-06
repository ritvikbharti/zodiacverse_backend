package com.ritvik.zodiacverseBackend.service;

import com.ritvik.zodiacverseBackend.dto.NotificationDto;
import com.ritvik.zodiacverseBackend.entity.Notification;
import com.ritvik.zodiacverseBackend.model.NotificationType;
import com.ritvik.zodiacverseBackend.model.User;           //  your model.User
import com.ritvik.zodiacverseBackend.repo.NotificationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepo notificationRepository;
    private final EmailService emailService;

    // ── Triggers ──────────────────────────────────────────────

    @Async
    public void notifyWelcome(User user) {
        create(user.getId(), NotificationType.WELCOME,
                "Welcome to ZodiacVerse! ✨",
                "Your cosmic journey has begun. Explore your birth chart and daily horoscope.",
                "/app/dashboard");
        // email already sent in AuthService — no duplicate here
    }

    @Async
    public void notifyPasswordChanged(User user) {
        create(user.getId(), NotificationType.PASSWORD_CHANGED,
                "Password Changed 🔒",
                "Your password was updated successfully. Contact support if this wasn't you.",
                "/app/settings");
        emailService.sendPasswordChangedEmail(
                user.getEmail(), user.getFullname()); //  getFullname() lowercase n
    }

    @Async
    public void notifyBookingConfirmed(User user, String astrologerName, String sessionTime) {
        create(user.getId(), NotificationType.BOOKING_CONFIRMED,
                "Booking Confirmed 🔮",
                "Your session with " + astrologerName + " is confirmed for " + sessionTime,
                "/app/bookings");
        emailService.sendBookingConfirmedEmail(
                user.getEmail(), user.getFullname(),
                astrologerName, sessionTime);
    }

    @Async
    public void notifyBookingReminder(User user, String astrologerName, String sessionTime) {
        create(user.getId(), NotificationType.BOOKING_REMINDER,
                "Session in 15 minutes ⏰",
                "Your session with " + astrologerName + " starts at " + sessionTime,
                "/app/bookings");
    }

    @Async
    public void notifyReportReady(User user, String reportName) {
        create(user.getId(), NotificationType.REPORT_READY,
                "Your Report is Ready 📊",
                reportName + " has been generated. Tap to view your cosmic insights.",
                "/app/reports");
        emailService.sendReportReadyEmail(
                user.getEmail(), user.getFullname(), reportName);
    }
    //  Add this method to NotificationService.java
    @Async
    public void notifyBookingCancelled(User user, String astrologerName, BigDecimal amount) {
        create(user.getId(), NotificationType.BOOKING_CANCELLED,
                "Booking Cancelled ",
                "Your booking with " + astrologerName +
                        " has been cancelled. ₹" + amount + " refunded to your wallet.",
                "/app/bookings");
    }

    // ── Read operations ───────────────────────────────────────

    public List<NotificationDto> getAll(UUID userId) {
        return notificationRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markRead(Long notificationId, UUID userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUserId().equals(userId)) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    @Transactional
    public void markAllRead(UUID userId) {
        notificationRepository.markAllReadByUserId(userId);
    }

    // ── Private helpers ───────────────────────────────────────

    private void create(UUID userId, NotificationType type,
                        String title, String body, String actionUrl) {
        Notification n = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .body(body)
                .actionUrl(actionUrl)
                .isRead(false)
                .build();
        notificationRepository.save(n);
        log.info("🔔 Notification [{}] created for userId={}", type, userId);
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .actionUrl(n.getActionUrl())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}