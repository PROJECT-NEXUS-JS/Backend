package com.example.nexus.notification.service;

import com.example.nexus.app.global.code.status.ErrorStatus;
import com.example.nexus.app.global.exception.GeneralException;
import com.example.nexus.app.user.domain.User;
import com.example.nexus.app.user.repository.UserRepository;
import com.example.nexus.notification.NotificationType;
import com.example.nexus.notification.domain.Notification;
import com.example.nexus.notification.dto.NotificationResponseDto;
import com.example.nexus.notification.repository.NotificationRepository;
import com.example.nexus.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmitterRepository emitterRepository;

    private static final Long DEFAULT_TIMEOUT = 60L * 30 * 1000;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        emitter.onTimeout(() -> emitterRepository.deleteById(userId));
        emitter.onError((e) -> emitterRepository.deleteById(userId));

        try {
            emitter.send(SseEmitter.event()
                    .id("0")
                    .name("connect")
                    .data("EventStream Connected. [userId=" + userId + "]"));
        } catch (Exception e) {
            emitterRepository.deleteById(userId);
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }

        return emitter;
    }

    @Transactional
    public void createNotification(Long userId, NotificationType type, String content, String link) {
        User user = findUserById(userId);

        Notification notification = Notification.builder()
                .user(user).type(type).content(content).link(link).build();
        notificationRepository.save(notification);

        sendNotification(userId, new NotificationResponseDto(notification));
    }

    @Transactional
    public void sendDirectMessageNotification(Long receiverId, String senderName, String messageContent, String messageLink) {
        User receiver = findUserById(receiverId);

        String content = String.format("%s님으로부터 새로운 메시지가 도착했습니다: %s", senderName, messageContent);
        Notification notification = Notification.builder()
                .user(receiver)
                .type(NotificationType.MESSAGE)
                .content(content)
                .link(messageLink)
                .build();
        notificationRepository.save(notification);

        sendNotification(receiverId, new NotificationResponseDto(notification));
    }

    private void sendNotification(Long userId, NotificationResponseDto responseDto) {
        SseEmitter emitter = emitterRepository.findById(userId);

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .id(String.valueOf(responseDto.getId()))
                        .name("sse")
                        .data(responseDto));
            } catch (Exception e) {
                emitterRepository.deleteById(userId);
            }
        }
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

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        Notification notification = findNotificationById(notificationId);
        validateNotificationOwner(userId, notification);
        notification.markAsRead();
    }

    @Transactional
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
