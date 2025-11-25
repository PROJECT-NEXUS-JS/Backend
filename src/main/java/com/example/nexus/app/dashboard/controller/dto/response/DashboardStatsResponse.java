package com.example.nexus.app.dashboard.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DashboardStatsResponse(
        @Schema(description = "찜하기 통계")
        StatItem likes,

        @Schema(description = "참여 대기 통계 (PENDING)")
        StatItem pendingApplications,

        @Schema(description = "참여자 통계 (APPROVED)")
        StatItem approvedParticipants,

        @Schema(description = "리뷰 통계")
        StatItem reviews,

        @Schema(description = "조회수 통계")
        StatItem views,

        @Schema(description = "리워드 지급 대기 통계 (PENDING)")
        StatItem pendingRewards,

        @Schema(description = "안 읽은 메시지 수")
        Long unreadMessages
) {
    public record StatItem(
            @Schema(description = "현재 수치")
            Long current,

            @Schema(description = "전일 수치")
            Long previousDay,

            @Schema(description = "증감 수치")
            Integer changeAmount
    ) {
        public static StatItem of(Long current, Long previousDay) {
            Long safeCurrent = current != null ? current : 0L;
            Long safePreviousDay = previousDay != null ? previousDay : 0L;
            int changeAmount = (int) (safeCurrent - safePreviousDay);
            return new StatItem(safeCurrent, safePreviousDay, changeAmount);
        }
    }

    public static DashboardStatsResponse of(
            Long totalLikes, Long yesterdayLikes,
            Long totalPendingApplications, Long yesterdayPendingApplications,
            Long totalApprovedParticipants, Long yesterdayApprovedParticipants,
            Long totalReviews, Long yesterdayReviews,
            Long totalViews, Long yesterdayViews,
            Long totalPendingRewards, Long yesterdayPendingRewards,
            Long totalUnreadMessages) {
        return new DashboardStatsResponse(
                StatItem.of(totalLikes, yesterdayLikes),
                StatItem.of(totalPendingApplications, yesterdayPendingApplications),
                StatItem.of(totalApprovedParticipants, yesterdayApprovedParticipants),
                StatItem.of(totalReviews, yesterdayReviews),
                StatItem.of(totalViews, yesterdayViews),
                StatItem.of(totalPendingRewards, yesterdayPendingRewards),
                totalUnreadMessages
        );
    }
}
