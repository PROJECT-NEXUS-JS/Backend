package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.Participation;
import com.example.nexus.app.post.domain.PrivacyItem;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

public record ParticipantPrivacyResponse(
        @Schema(description = "참여 ID")
        Long participationId,

        @Schema(description = "게시글 ID")
        Long postId,

        @Schema(description = "신청자 이름")
        String applicantName,

        @Schema(description = "연락처")
        String contactNumber,

        @Schema(description = "이메일")
        String applicantEmail,

        @Schema(description = "신청 사유")
        String applicationReason
) {
    public static ParticipantPrivacyResponse from(Participation participation, Set<PrivacyItem> collectedItems) {
        return new ParticipantPrivacyResponse(
                participation.getId(),
                participation.getPost().getId(),
                participation.getApplicantName(),
                participation.getContactNumber(),
                collectedItems.contains(PrivacyItem.EMAIL) ? participation.getApplicantEmail() : null,
                participation.getApplicationReason()
        );
    }
}
