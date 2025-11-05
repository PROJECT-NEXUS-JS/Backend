package com.example.nexus.app.message.controller.doc;

import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "메시지 SSE", description = "메시지 실시간 알림 API")
public interface SseControllerDoc {
    @Operation(
            summary = "SSE 구독",
            description = """
                    실시간 메시지 알림을 받기 위한 SSE 연결을 생성합니다.
                    연결 성공 시 CONNECT 이벤트를 받으며, 이후 MESSAGE, READ_STATUS, ROOM_UPDATE 이벤트를 실시간으로 수신합니다.

                    **SseEventType (이벤트 타입):**
                    - `CONNECT`: 연결
                    - `MESSAGE`: 메시지
                    - `READ_STATUS`: 읽음 상태
                    - `ROOM_UPDATE`: 채팅방 업데이트
                    - `HEARTBEAT`: 하트비트
                    """
    )
    SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails customUserDetails);
}
