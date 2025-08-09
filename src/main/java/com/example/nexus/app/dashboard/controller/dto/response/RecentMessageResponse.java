package com.example.nexus.app.dashboard.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RecentMessageResponse(
        @Schema(description = "메시지 방 ID")
        Long messageRoomId,

        @Schema(description = "발신자 닉네임")
        String senderNickname,

        @Schema(description = "발신자 프로필 이미지")
        String senderProfileImage,

        @Schema(description = "메시지 내용")
        String content,

        @Schema(description = "전송 시간")
        LocalDateTime sentAt,

        @Schema(description = "읽음 여부")
        Boolean isRead
) {
    public static RecentMessageResponse of(Long messageRoomId, String senderNickname, String senderProfileImage, String content,
                                           LocalDateTime sentAt, Boolean isRead) {
        return new RecentMessageResponse(messageRoomId, senderNickname, senderProfileImage, content, sentAt, isRead);
    }
}
