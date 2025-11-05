package com.example.nexus.app.message.service;

import com.example.nexus.app.global.config.SseConfig;
import com.example.nexus.app.message.controller.dto.response.MessageResponse;
import com.example.nexus.app.message.controller.dto.response.MessageRoomResponse;
import com.example.nexus.app.message.controller.dto.response.SseEventDto;
import com.example.nexus.app.message.domain.SseEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final SseConfig sseConfig;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter createEmitter(Long userId) {
        // 기존 연결이 있으면 명시적으로 종료
        SseEmitter oldEmitter = emitters.get(userId);
        if (oldEmitter != null) {
            try {
                oldEmitter.complete();
            } catch (Exception e) {
                log.error("기존 SSE 연결 종료에 실패했습니다. - userId: {}, error: {}", userId, e.getMessage());
            }
        }

        SseEmitter emitter = new SseEmitter(sseConfig.getTimeout());

        emitter.onTimeout(() -> {
            removeEmitter(userId);
        });

        emitter.onError((e) -> {
            log.warn("SSE 연결 중 오류가 발생했습니다. - userId: {}, error: {}", userId, e.getMessage());
            removeEmitter(userId);
        });

        emitter.onCompletion(() -> {
            removeEmitter(userId);
        });

        emitters.put(userId, emitter);
        sendConnectEvent(userId);

        return emitter;
    }

    public void sendMessage(Long userId, MessageResponse message) {
        sendEvent(userId, SseEventType.MESSAGE, message);
    }

    public void sendReadStatus(Long userId, Long roomId, Integer unreadCount) {
        Map<String, Object> data = Map.of(
                "roomId", roomId,
                "unreadCount", unreadCount
        );
        sendEvent(userId, SseEventType.READ_STATUS, data);
    }

    public void sendRoomUpdate(Long userId, MessageRoomResponse room) {
        sendEvent(userId, SseEventType.ROOM_UPDATE, room);
    }

    // 지정된 시간이 지나면 다시 실행
    @Scheduled(fixedRateString = "${sse.heartbeat:20000}")
    public void sendHeartbeat() {
        if (emitters.isEmpty()) {
            return;
        }

        // ConcurrentModificationException 방지
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(SseEventType.HEARTBEAT.name())
                        .data(SseEventDto.heartbeat())
                        .comment("keep-alive"));
            } catch (IOException e) {
                log.error("하트비트 전송에 실패했습니다. - userId: {}", userId);
                removeEmitter(userId);
            } catch (Exception e) {
                log.error("하트비트 전송 중 예상치 못한 오류가 발생했습니다. - userId: {}, error: {}", userId, e.getMessage());
                removeEmitter(userId);
            }
        });
    }

    private void sendConnectEvent(Long userId) {
        try {
            SseEmitter emitter = emitters.get(userId);
            if (emitter != null) {
                emitter.send(SseEmitter.event()
                        .name(SseEventType.CONNECT.name())
                        .data(SseEventDto.connect()));
            }
        } catch (IOException e) {
            log.error("연결 이벤트 전송에 실패했습니다. - userId: {}", userId);
            removeEmitter(userId);
        }
    }

    private void sendEvent(Long userId, SseEventType eventType, Object data) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name(eventType.name())
                    .data(SseEventDto.of(eventType, data)));
        } catch (IOException e) {
            log.error("SSE 이벤트 전송에 실패했습니다. - userId: {}, eventType: {}, error: {}", userId, eventType, e.getMessage());
            removeEmitter(userId);
        } catch (Exception e) {
            log.error("SSE 이벤트 전송 중 예상치 못한 오류가 발생했습니다. - userId: {}, eventType: {}, error: {}", userId, eventType, e.getMessage());
            removeEmitter(userId);
        }
    }

    private void removeEmitter(Long userId) {
        emitters.remove(userId);
    }

    public int getConnectionCount() {
        return emitters.size();
    }
}
