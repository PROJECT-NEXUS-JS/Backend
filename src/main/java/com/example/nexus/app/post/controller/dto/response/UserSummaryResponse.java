package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserSummaryResponse(
        @Schema(description = "사용자 ID")
        Long id,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "프로필 이미지 URL")
        String profileUrl
) {

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl()
        );
    }
}
