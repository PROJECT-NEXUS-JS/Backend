package com.example.nexus.app.message.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "SSE 타입")
public enum SseEventType {
    @Schema(description = "연결")
    CONNECT("연결"),

    @Schema(description = "메시지")
    MESSAGE("메시지"),

    @Schema(description = "읽음 상태")
    READ_STATUS("읽음 상태"),

    @Schema(description = "채팅방 업데이트")
    ROOM_UPDATE("채팅방 업데이트"),

    @Schema(description = "하트비트")
    HEARTBEAT("하트비트");

    @Schema(description = "이벤트 타입 설명")
    private final String description;
}
