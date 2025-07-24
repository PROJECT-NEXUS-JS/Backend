package com.example.nexus.notification.dto;

import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.domain.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {
    private final Long id;
    private final NotificationType type;
    private final String content;
    private final boolean isRead;
    private final String link;
    private final LocalDateTime createdAt;

    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.type = notification.getType();
        this.content = notification.getContent();
        this.isRead = notification.isRead();
        this.link = notification.getLink();
        this.createdAt = notification.getCreatedAt();
    }
}
