package com.example.nexus.app.dashboard.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record WaitingParticipantResponse(
        @Schema(description = "참여자 ID")
        Long participantId,

        @Schema(description = "참여자 닉네임")
        String nickname,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "신청일시")
        LocalDateTime appliedAt

) {
    public static WaitingParticipantResponse of(Long participantId, String nickname, String profileImageUrl,
                                                LocalDateTime appliedAt) {
        return new WaitingParticipantResponse(participantId, nickname, profileImageUrl, appliedAt);
    }
}
