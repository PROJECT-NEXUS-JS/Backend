package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.Participation;
import com.example.nexus.app.post.domain.ParticipationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ParticipationResponse(
        @Schema(description = "참여 ID")
        Long id,

        @Schema(description = "신청 일시")
        LocalDateTime appliedAt,

        @Schema(description = "승인 일시")
        LocalDateTime approvedAt,

        @Schema(description = "참여 상태")
        ParticipationStatus status,

        @Schema(description = "신청자 이름")
        String applicantName,

        @Schema(description = "연락처")
        String contactNumber,

        @Schema(description = "신청 사유")
        String applicationReason,

        @Schema(description = "게시글 정보")
        PostSummaryResponse post,

        @Schema(description = "참여자 정보")
        UserSummaryResponse user
) {

    public static ParticipationResponse from(Participation participation) {
        return new ParticipationResponse(
                participation.getId(),
                participation.getAppliedAt(),
                participation.getApprovedAt(),
                participation.getStatus(),
                participation.getApplicantName(),
                participation.getContactNumber(),
                participation.getApplicationReason(),
                PostSummaryResponse.from(participation.getPost()),
                UserSummaryResponse.from(participation.getUser())
        );
    }
}
