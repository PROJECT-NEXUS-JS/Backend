package com.example.nexus.app.participation.controller.dto.response;

import com.example.nexus.app.participation.domain.Participation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "참여자 상세 정보 응답")
public record ParticipantDetailResponse(
        @Schema(description = "참여 ID")
        Long participationId,

        @Schema(description = "게시글 ID")
        Long postId,

        @Schema(description = "사용자 ID")
        Long userId,

        @Schema(description = "신청자 이름")
        String applicantName,

        @Schema(description = "연락처")
        String contactNumber,

        @Schema(description = "이메일")
        String applicantEmail,

        @Schema(description = "신청 사유")
        String applicationReason,

        @Schema(description = "신청 일시")
        LocalDateTime appliedAt,

        @Schema(description = "개인정보 수집 및 이용 동의")
        Boolean privacyAgreement,

        @Schema(description = "참여 조건 및 규칙 동의")
        Boolean termsAgreement
) {
    public static ParticipantDetailResponse from(Participation participation) {
        return new ParticipantDetailResponse(
                participation.getId(),
                participation.getPost().getId(),
                participation.getUser().getId(),
                participation.getApplicantName(),
                participation.getContactNumber(),
                participation.getApplicantEmail(),
                participation.getApplicationReason(),
                participation.getAppliedAt(),
                participation.getPrivacyAgreement(),
                participation.getTermsAgreement()
        );
    }
}
