package com.example.nexus.app.participation.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "참가 신청 통계")
public record ParticipationStatisticsResponse(
        @Schema(description = "승인 대기 인원", example = "5")
        Long pendingCount,

        @Schema(description = "진행중 인원", example = "10")
        Long approvedCount,

        @Schema(description = "피드백 완료 인원", example = "7")
        Long feedbackCompletedCount,

        @Schema(description = "테스트 완료 인원", example = "8")
        Long testCompletedCount,

        @Schema(description = "거절됨 인원", example = "3")
        Long rejectedCount,

        @Schema(description = "전체 신청 인원", example = "33")
        Long totalCount
) {
    public static ParticipationStatisticsResponse of(Long pendingCount, Long approvedCount,
                                                     Long feedbackCompletedCount, Long testCompletedCount,
                                                     Long rejectedCount) {
        Long total = pendingCount + approvedCount + feedbackCompletedCount + testCompletedCount + rejectedCount;
        return new ParticipationStatisticsResponse(
                pendingCount,
                approvedCount,
                feedbackCompletedCount,
                testCompletedCount,
                rejectedCount,
                total
        );
    }
}
