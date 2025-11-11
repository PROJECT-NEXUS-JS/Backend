package com.example.nexus.app.datacenter.controller.dto.response.datacenter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * 품질 피드백 응답 DTO
 */
@Builder
@Schema(description = "품질 피드백 데이터")
public record QualityFeedbackResponse(
        @Schema(description = "불편 요소 Top 3 (요소명: 개수)")
        Map<String, Long> topInconvenientElements,

        @Schema(description = "버그 존재 비율 (%)")
        Double bugExistenceRate,

        @Schema(description = "버그 있음 건수")
        Long bugExistCount,

        @Schema(description = "버그 없음 건수")
        Long noBugCount,

        @Schema(description = "만족도 점수 분포 (1~5점: 비율%)")
        Map<Integer, Double> satisfactionScoreDistribution,

        @Schema(description = "문제 유형 비중 (유형명: 비율%)")
        Map<String, Double> problemTypeProportions,

        @Schema(description = "주요 문제 발생 위치 리스트 (최대 5개)")
        List<ProblemLocationDto> topProblemLocations,

        @Schema(description = "스크린샷 미리보기 URL 리스트 (최대 3개)")
        List<String> screenshotPreviews
) {

    @Builder
    public record ProblemLocationDto(
            @Schema(description = "문제 발생 위치")
            String location,

            @Schema(description = "문제 유형")
            String problemType,

            @Schema(description = "신고 횟수")
            Long reportCount
    ) {
    }
}

