package com.example.nexus.app.message.controller;

import com.example.nexus.app.global.oauth.domain.CustomUserDetails;
import com.example.nexus.app.message.controller.doc.SseControllerDoc;
import com.example.nexus.app.message.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/v1/users/sse")
@RequiredArgsConstructor
public class SseController implements SseControllerDoc {

    private final SseEmitterService sseEmitterService;

    // text/event-stream (SSE 표준 미디어 타입)
    @Override
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Long userId = customUserDetails.getUserId();
        return sseEmitterService.createEmitter(userId);
    }
}
