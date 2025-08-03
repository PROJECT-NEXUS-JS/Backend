package com.example.nexus.app.message.controller.dto.response;

import com.example.nexus.app.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record RoomUserInfo(
        @Schema(description = "사용자 ID")
        Long userId,

        @Schema(description = "사용자 이름")
        String userName,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl
) {
    public static RoomUserInfo from(User user) {
        return new RoomUserInfo(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl()
        );
    }
}
