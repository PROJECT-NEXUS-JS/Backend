package com.example.nexus.app.dashboard.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BarChartResponse(
        @Schema(description = "막대 차트 데이터")
        List<BarChartItem> items
) {
    public record BarChartItem(
            @Schema(description = "카테고리명 (조회수, 스크랩, 신청자, 참여자, 리뷰)")
            String category,

            @Schema(description = "수치")
            Long value
    ) {
        public static BarChartItem of(String category, Long value) {
            return new BarChartItem(category, value);
        }
    }

    public static BarChartResponse of(Long views, Long likes, Long pending, Long approved, Long reviews) {
        List<BarChartItem> items = List.of(
                BarChartItem.of("조회수", views),
                BarChartItem.of("스크랩", likes),
                BarChartItem.of("신청자", pending),
                BarChartItem.of("참여자", approved),
                BarChartItem.of("리뷰", reviews)
        );
        return new BarChartResponse(items);
    }
}
