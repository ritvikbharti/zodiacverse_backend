package com.ritvik.zodiacverseBackend.dto;

import com.ritvik.zodiacverseBackend.model.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private NotificationType type;
    private String title;
    private String body;
    private String actionUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
}