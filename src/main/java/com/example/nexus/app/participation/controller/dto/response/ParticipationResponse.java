package com.example.nexus.app.participation.controller.dto.response;

import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import com.example.nexus.app.post.controller.dto.response.PostDetailResponse;
import com.example.nexus.app.post.controller.dto.response.UserSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ParticipationResponse(
        @Schema(description = "참여 ID")
        Long id,

        @Schema(description = "신청 일시")
        LocalDateTime appliedAt,

        @Schema(description = "승인 일시")
        LocalDateTime approvedAt,

        @Schema(description = "완료 일시")
        LocalDateTime completedAt,

        @Schema(description = "지급 일시")
        LocalDateTime paidAt,

        @Schema(description = "참여 상태")
        ParticipationStatus status,

        @Schema(description = "리워드 지급 여부")
        Boolean isPaid,

        @Schema(description = "신청자 이름")
        String applicantName,

        @Schema(description = "연락처")
        String contactNumber,

        @Schema(description = "이메일")
        String applicantEmail,

        @Schema(description = "신청 사유")
        String applicationReason,

        @Schema(description = "게시글 정보")
        PostDetailResponse post,

        @Schema(description = "참여자 정보")
        UserSummaryResponse user
) {

    public static ParticipationResponse from(Participation participation, Boolean isLiked, Boolean isParticipated, Long currentViewCount) {
        return new ParticipationResponse(
                participation.getId(),
                participation.getAppliedAt(),
                participation.getApprovedAt(),
                participation.getCompletedAt(),
                participation.getPaidAt(),
                participation.getStatus(),
                participation.getIsPaid(),
                participation.getApplicantName(),
                participation.getContactNumber(),
                participation.getApplicantEmail(),
                participation.getApplicationReason(),
                PostDetailResponse.from(participation.getPost(), isLiked, isParticipated, currentViewCount),
                UserSummaryResponse.from(participation.getUser())
        );
    }
}
