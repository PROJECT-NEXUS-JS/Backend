package com.example.nexus.app.message.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TodayMessageResponse(
        @Schema(description = "메시지 ID")
        Long messageId,

        @Schema(description = "발신자 이름")
        String senderName,

        @Schema(description = "메시지 내용 미리보기")
        String messagePreview,

        @Schema(description = "수신 시간")
        LocalDateTime receivedAt,

        @Schema(description = "읽음 여부")
        Boolean isRead
) {}
