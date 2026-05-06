package com.ritvik.zodiacverseBackend.controller;

import com.ritvik.zodiacverseBackend.dto.NotificationDto;
import com.ritvik.zodiacverseBackend.model.User;
import com.ritvik.zodiacverseBackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ── Resolves User from authentication.getDetails() ────────
    // JwtAuthFilter stores the full User entity in details
    // so we never need a DB lookup here
    private User resolveUser(Authentication auth) {
        if (auth == null) {
            throw new RuntimeException("Not authenticated");
        }
        Object details = auth.getDetails();
        if (details instanceof User) {
            return (User) details;
        }
        // Should never happen if JwtAuthFilter is correct
        log.error("❌ details is not User — type: {}",
                details == null ? "null" : details.getClass().getName());
        throw new RuntimeException("Could not resolve user from token");
    }

    // ── GET /api/v1/notifications ──────────────────────────────
    @GetMapping
    public ResponseEntity<?> getAll(Authentication auth) {
        User user = resolveUser(auth);
        List<NotificationDto> list = notificationService.getAll(user.getId());
        long unread = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", list,
                "unreadCount", unread
        ));
    }

    // ── GET /api/v1/notifications/unread-count ─────────────────
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication auth) {
        User user = resolveUser(auth);
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "unreadCount", count
        ));
    }

    // ── PUT /api/v1/notifications/{id}/read ────────────────────
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markRead(
            @PathVariable Long id,
            Authentication auth) {
        User user = resolveUser(auth);
        notificationService.markRead(id, user.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ── PUT /api/v1/notifications/read-all ─────────────────────
    @PutMapping("/read-all")
    public ResponseEntity<?> markAllRead(Authentication auth) {
        User user = resolveUser(auth);
        notificationService.markAllRead(user.getId());
        return ResponseEntity.ok(Map.of("success", true));
    }
}