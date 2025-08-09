package com.example.nexus.app.dashboard.controller.dto.response;

import com.example.nexus.app.post.domain.ParticipantReward;
import com.example.nexus.app.post.domain.Participation;
import com.example.nexus.app.post.domain.ParticipationStatus;
import com.example.nexus.app.post.domain.RewardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ParticipantDetailResponse(
        @Schema(description = "참여 ID")
        Long participationId,

        @Schema(description = "참여자 정보")
        ParticipantInfo participant,

        @Schema(description = "참여 상태")
        ParticipationStatus status,

        @Schema(description = "신청일시")
        LocalDateTime appliedAt,

        @Schema(description = "승인일시")
        LocalDateTime approvedAt,

        @Schema(description = "완료일시")
        LocalDateTime completedAt,

        @Schema(description = "리워드 상태")
        RewardStatus rewardStatus,

        @Schema(description = "지급일시")
        LocalDateTime paidAt,

        @Schema(description = "신청 사유")
        String applicationReason
) {
    public record ParticipantInfo(
            @Schema(description = "사용자 ID")
            Long userId,

            @Schema(description = "닉네임")
            String nickname,

            @Schema(description = "프로필 이미지 URL")
            String profileUrl
    ) {
        public static ParticipantInfo from(Participation participation) {
            return new ParticipantInfo(
                    participation.getUser().getId(),
                    participation.getUser().getNickname(),
                    participation.getUser().getProfileUrl()
            );
        }
    }

    public static ParticipantDetailResponse from(Participation participation, ParticipantReward participantReward) {
        return new ParticipantDetailResponse(
                participation.getId(),
                ParticipantInfo.from(participation),
                participation.getStatus(),
                participation.getAppliedAt(),
                participation.getApprovedAt(),
                participantReward != null ? participantReward.getCompletedAt() : null,
                participantReward != null ? participantReward.getRewardStatus() : null,
                participantReward != null ? participantReward.getPaidAt() : null,
                participation.getApplicationReason()
        );
    }
}
