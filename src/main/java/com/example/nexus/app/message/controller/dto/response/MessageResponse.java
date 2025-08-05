package com.example.nexus.app.message.controller.dto.response;

import com.example.nexus.app.message.domain.Message;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MessageResponse(
        @Schema(description = "메시지 ID")
        Long messageId,

        @Schema(description = "발신자 정보")
        MessageSenderInfo sender,

        @Schema(description = "메시지 내용")
        String content,

        @Schema(description = "메시지 타입")
        String messageType,

        @Schema(description = "파일 정보 (파일 메시지인 경우)")
        MessageFileInfo fileInfo,

        @Schema(description = "읽음 여부")
        Boolean isRead,

        @Schema(description = "읽은 시간")
        LocalDateTime readAt,

        @Schema(description = "전송 시간")
        LocalDateTime sentAt,

        @Schema(description = "내가 보낸 메시지 여부")
        Boolean isMine
) {
    public static MessageResponse from(Message message, Long currentUserId) {
        return new MessageResponse(
                message.getId(),
                MessageSenderInfo.from(message.getSender()),
                message.getContent(),
                message.getMessageType().name(),
                message.isFileMessage() ? MessageFileInfo.from(message) : null,
                message.getIsRead(),
                message.getReadAt(),
                message.getCreatedAt(),
                message.getSender().getId().equals(currentUserId)
        );
    }
}
