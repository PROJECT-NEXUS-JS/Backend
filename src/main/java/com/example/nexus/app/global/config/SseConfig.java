package com.example.nexus.app.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@Getter
public class SseConfig {
    @Value("${sse.timeout:0}")
    private Long timeout;

    @Value("${sse.heartbeat:20000}")
    private Long heartbeatInterval;
}
