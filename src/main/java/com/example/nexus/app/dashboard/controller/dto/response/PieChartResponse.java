package com.example.nexus.app.dashboard.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PieChartResponse(
        @Schema(description = "상태별 원형 차트")
        PieChartData statusChart,

        @Schema(description = "리워드별 원형 차트")
        PieChartData rewardChart
) {
    public record PieChartData(
            @Schema(description = "차트 제목")
            String title,

            @Schema(description = "차트 데이터")
            List<PieChartItem> items
    ) { }

    public record PieChartItem(
            @Schema(description = "항목명")
            String label,

            @Schema(description = "수치")
            Long value
    ) { }

    public static PieChartResponse of(PieChartData statusChart, PieChartData rewardChart) {
        return new PieChartResponse(statusChart, rewardChart);
    }
}
