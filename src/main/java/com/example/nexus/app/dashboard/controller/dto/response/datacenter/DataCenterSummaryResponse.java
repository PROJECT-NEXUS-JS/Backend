package com.example.nexus.app.dashboard.controller.dto.response.datacenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

/**
 * 데이터센터 요약 카드 응답 DTO
 */
@Builder
@Schema(description = "데이터센터 요약 정보")
public record DataCenterSummaryResponse(
        @Schema(description = "총 참여자 수")
        Long totalParticipants,

        @Schema(description = "전주 대비 참여자 증가율 (%)")
        Double participantChangeRate,

        @Schema(description = "이번주 참여자 수")
        Long thisWeekParticipants,

        @Schema(description = "평균 전체 만족도 (1-5점)")
        Double averageSatisfaction,

        @Schema(description = "전주 대비 만족도 증가율 (%)")
        Double satisfactionChangeRate,

        @Schema(description = "버그 발생률 (%)")
        Double bugOccurrenceRate,

        @Schema(description = "전주 대비 버그율 증가율 (%)")
        Double bugRateChangeRate,

        @Schema(description = "총 피드백 수")
        Long totalFeedbacks,

        @Schema(description = "버그 건수")
        Long bugCount,

        @Schema(description = "긍정 피드백 비율 (%, 만족도 4점 이상)")
        Double positiveFeedbackRate,

        @Schema(description = "전주 대비 긍정 피드백 비율 증가율 (%)")
        Double positiveFeedbackChangeRate,

        @Schema(description = "긍정 피드백 수")
        Long positiveFeedbackCount
) {
}

