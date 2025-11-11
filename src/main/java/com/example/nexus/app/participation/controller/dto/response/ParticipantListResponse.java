package com.example.nexus.app.participation.controller.dto.response;

import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.reward.domain.ParticipantReward;
import com.example.nexus.app.reward.domain.RewardStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ParticipantListResponse(
        @Schema(description = "참여 ID")
        Long participationId,

        @Schema(description = "게시글 ID")
        Long postId,

        @Schema(description = "참여자 ID")
        Long userId,

        @Schema(description = "참여자 닉네임")
        String nickname,

        @Schema(description = "신청 시 작성한 이메일")
        String applicantEmail,

        @Schema(description = "신청일시")
        LocalDateTime appliedAt,

        @Schema(description = "승인일시")
        LocalDateTime approvedAt,

        @Schema(description = "완료일시")
        LocalDateTime completedAt,

        @Schema(description = "참여 상태")
        ParticipationStatus participationStatus,

        @Schema(description = "리워드 지급 상태")
        RewardStatus rewardStatus,

        @Schema(description = "리워드 지급일")
        LocalDateTime paidAt,

        @Schema(description = "지급 완료 여부")
        Boolean isPaid
) {
    public static ParticipantListResponse from(Participation participation, ParticipantReward participantReward) {
        return new ParticipantListResponse(
                participation.getId(),
                participation.getPost().getId(),
                participation.getUser().getId(),
                participation.getUser().getNickname(),
                participation.getApplicantEmail(),
                participation.getAppliedAt(),
                participation.getApprovedAt(),
                participantReward != null ? participantReward.getCompletedAt() : participation.getCompletedAt(),
                participation.getStatus(),
                participantReward != null ? participantReward.getRewardStatus() : null,
                participantReward != null ? participantReward.getPaidAt() : null,
                participation.getIsPaid()
        );
    }
}
