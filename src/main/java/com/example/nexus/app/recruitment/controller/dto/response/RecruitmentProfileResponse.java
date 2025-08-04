package com.example.nexus.app.recruitment.controller.dto.response;

import com.example.nexus.app.recruitment.domain.RecruitmentProfile;
import com.example.nexus.app.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RecruitmentProfileResponse(
        @Schema(description = "사용자 ID")
        Long userId,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "호스트 소개글")
        String introduction,

        @Schema(description = "프로필 생성일")
        LocalDateTime createdAt,

        @Schema(description = "프로필 수정일")
        LocalDateTime updatedAt
) {

    public static RecruitmentProfileResponse from(User user, RecruitmentProfile profile) {
        return new RecruitmentProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl(),
                profile.getIntroduction(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    public static RecruitmentProfileResponse from(User user) {
        return new RecruitmentProfileResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileUrl(),
                "", // 기본 소개글
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
