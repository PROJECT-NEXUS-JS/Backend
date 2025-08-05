package com.example.nexus.app.message.controller.dto.response;

import com.example.nexus.app.message.domain.MessageRoom;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MessageRoomResponse(
        @Schema(description = "채팅방 ID")
        Long roomId,

        @Schema(description = "게시글 정보")
        RoomPostInfo postInfo,

        @Schema(description = "상대방 정보")
        RoomUserInfo otherUser,

        @Schema(description = "마지막 메시지")
        String lastMessage,

        @Schema(description = "마지막 메시지 시간")
        LocalDateTime lastMessageAt,

        @Schema(description = "안읽은 메시지 수")
        Integer unreadCount,

        @Schema(description = "채팅방 생성일")
        LocalDateTime createdAt
) {
    public static MessageRoomResponse from(MessageRoom room, Long currentUserId) {
        return new MessageRoomResponse(
                room.getId(),
                RoomPostInfo.from(room.getPost()),
                RoomUserInfo.from(room.getOtherUser(currentUserId)),
                room.getLastMessage(),
                room.getLastMessageAt(),
                room.getUnreadCountForUser(currentUserId),
                room.getCreatedAt()
        );
    }
}
