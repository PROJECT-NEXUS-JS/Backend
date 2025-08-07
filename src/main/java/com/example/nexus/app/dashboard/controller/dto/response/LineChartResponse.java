package com.example.nexus.app.dashboard.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record LineChartResponse(
        @Schema(description = "선형 차트 데이터")
        List<LineChartSeries> series,

        @Schema(description = "날짜 라벨")
        List<LocalDate> labels
) {
    public record LineChartSeries(
            @Schema(description = "시리즈명")
            String name,

            @Schema(description = "데이터 포인트")
            List<Long> data
    ) { }

    public static LineChartResponse of(List<LocalDate> labels, List<Long> likesData, List<Long> applicationsData, List<Long> viewsData) {
        List<LineChartSeries> series = List.of(
                new LineChartSeries("찜하기", likesData),
                new LineChartSeries("신청", applicationsData),
                new LineChartSeries("조회수", viewsData)
        );
        return new LineChartResponse(series, labels);
    }
}
