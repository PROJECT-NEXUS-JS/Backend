package com.example.nexus.notification.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.domain.Notification;
import com.example.nexus.notification.dto.NotificationResponseDto;
import com.example.nexus.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(Long userId, NotificationType type, String content, String link) {
        User user = findUserById(userId);
        Notification notification = Notification.builder()
                .user(user).type(type).content(content).link(link).build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getNotifications(Long userId) {
        User user = findUserById(userId);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(NotificationResponseDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NotificationResponseDto getNotification(Long userId, Long notificationId) {
        Notification notification = findNotificationById(notificationId);
        validateNotificationOwner(userId, notification);
        return new NotificationResponseDto(notification);
    }

    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = findNotificationById(notificationId);
        validateNotificationOwner(userId, notification);
        notification.markAsRead();
    }

    public void markAllAsRead(Long userId) {
        User user = findUserById(userId);
        notificationRepository.markAllAsReadByUser(user);
    }

    @Transactional(readOnly = true)
    public long getUnreadNotificationCount(Long userId) {
        User user = findUserById(userId);
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    }

    private Notification findNotificationById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.NOTIFICATION_NOT_FOUND));
    }

    private void validateNotificationOwner(Long userId, Notification notification) {
        if (!notification.getUser().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN);
        }
    }
}
