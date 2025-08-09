package com.example.nexus.app.post.domain.dto;

import com.example.nexus.app.post.controller.dto.request.ParticipationApplicationRequest;

public record ParticipationApplicationDto(
        String applicantName,
        String contactNumber,
        String applicantEmail,
        String applicationReason,
        Boolean privacyAgreement,
        Boolean termsAgreement
) {

    public static ParticipationApplicationDto from(ParticipationApplicationRequest request) {
        return new ParticipationApplicationDto(
                request.applicantName(),
                request.contactNumber(),
                request.applicantEmail(),
                request.applicationReason(),
                request.privacyAgreement(),
                request.termsAgreement()
        );
    }
}
