package com.example.nexus.app.message.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MessageSendRequest(
        @Schema(description = "메시지 내용", required = true)
        @NotBlank
        String content
) {}
