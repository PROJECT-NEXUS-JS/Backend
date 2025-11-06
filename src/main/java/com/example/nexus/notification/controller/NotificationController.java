package com.example.nexus.notification.controller;

import com.example.nexus.app.global.code.dto.ApiResponse;
import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.notification.dto.NotificationCountDto;
import com.example.nexus.notification.dto.NotificationResponseDto;
import com.example.nexus.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Tag(name = "알림", description = "사용자 알림 및 실시간 메시지 푸시 관련 API")
@RestController
@RequestMapping("/v1/users/my/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // SSE 구독 엔드포인트 (실시간 알림/메시지 수신)
    @Operation(summary = "알림 구독 (SSE 연결)", description = "클라이언트가 실시간 알림 및 쪽지 도착 푸시를 받기 위해 구독하는 엔드포인트")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 로그인한 사용자 ID를 기반으로 SSE 연결 및 Emitter 객체 반환
        return notificationService.subscribe(userDetails.getUserId());
    }

    @Operation(summary = "내 모든 알림 목록 조회")
    @GetMapping
    public ApiResponse<List<NotificationResponseDto>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationResponseDto> notifications = notificationService.getNotifications(userDetails.getUserId());
        return ApiResponse.onSuccess(notifications);
    }

    @Operation(summary = "읽지 않은 알림 개수 조회")
    @GetMapping("/unread-count")
    public ApiResponse<NotificationCountDto> getUnreadNotificationCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = notificationService.getUnreadNotificationCount(userDetails.getUserId());
        return ApiResponse.onSuccess(new NotificationCountDto(count));
    }

    @Operation(summary = "특정 알림 상세 조회")
    @GetMapping("/{notificationId}")
    public ApiResponse<NotificationResponseDto> getNotification(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        NotificationResponseDto notification = notificationService.getNotification(userDetails.getUserId(), notificationId);
        return ApiResponse.onSuccess(notification);
    }

    @Operation(summary = "특정 알림 읽음 처리")
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Object> markAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long notificationId) {
        notificationService.markAsRead(userDetails.getUserId(), notificationId);
        return ApiResponse.onSuccess(null);
    }

    @Operation(summary = "모든 알림 읽음 처리")
    @PostMapping("/read-all")
    public ApiResponse<Object> markAllAsRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllAsRead(userDetails.getUserId());
        return ApiResponse.onSuccess(null);
    }
}
