package com.example.nexus.app.participation.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "참가 신청 통계")
public record ParticipationStatisticsResponse(
        @Schema(description = "승인 대기 인원")
        Long pendingCount,

        @Schema(description = "진행중 인원", example = "10")
        Long approvedCount,

        @Schema(description = "완료 (지급 대기) 인원", example = "8")
        Long completedCount,

        @Schema(description = "지급 완료 인원", example = "15")
        Long paidCount,

        @Schema(description = "거절됨 인원", example = "3")
        Long rejectedCount,

        @Schema(description = "전체 신청 인원", example = "41")
        Long totalCount
) {
    public static ParticipationStatisticsResponse of (Long pendingCount, Long approvedCount,
                                                      Long completedCount, Long paidCount,
                                                      Long rejectedCount) {
        Long total = pendingCount + approvedCount + completedCount + paidCount + rejectedCount;
        return new ParticipationStatisticsResponse(
                pendingCount,
                approvedCount,
                completedCount,
                paidCount,
                rejectedCount,
                total
        );
    }
}
