package com.example.nexus.app.message.controller.dto.response;

import com.example.nexus.app.message.domain.SseEventType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record SseEventDto(
        @Schema(description = "이벤트 타입", example = "MESSAGE")
        String eventType,

        @Schema(description = "이벤트 데이터")
        Object data,

        @Schema(description = "타임스탬프")
        LocalDateTime timestamp
) {
    public static SseEventDto of(SseEventType type, Object data) {
        return new SseEventDto(type.name(), data, LocalDateTime.now());
    }

    public static SseEventDto connect() {
        return new SseEventDto(SseEventType.CONNECT.name(), "Connected", LocalDateTime.now());
    }

    public static SseEventDto heartbeat() {
        return new SseEventDto(SseEventType.HEARTBEAT.name(), "Ping", LocalDateTime.now());
    }
}
