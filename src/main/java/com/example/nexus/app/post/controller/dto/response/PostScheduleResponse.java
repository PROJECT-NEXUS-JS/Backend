package com.example.nexus.app.post.controller.dto.response;

import com.example.nexus.app.post.domain.PostSchedule;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PostScheduleResponse(
        @Schema(description = "시작 날짜")
        LocalDateTime startDate,

        @Schema(description = "종료 날짜")
        LocalDateTime endDate,

        @Schema(description = "모집 마감일")
        LocalDateTime recruitmentDeadline,

        @Schema(description = "소요 시간")
        String durationTime
) {
    public static PostScheduleResponse from(PostSchedule schedule) {
        return new PostScheduleResponse(
                schedule.getStartDate(),
                schedule.getEndDate(),
                schedule.getRecruitmentDeadline(),
                schedule.getDurationTime()
        );
    }
}
