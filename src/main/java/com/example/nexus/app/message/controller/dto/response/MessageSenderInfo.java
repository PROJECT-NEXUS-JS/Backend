package com.example.nexus.app.message.controller.dto.response;

import com.example.nexus.app.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record MessageSenderInfo(
        @Schema(description = "발신자 ID")
        Long userId,

        @Schema(description = "발신자 이름")
        String userName,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl
) {
    public static MessageSenderInfo from(User user) {
        return new MessageSenderInfo(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl()
        );
    }
}
