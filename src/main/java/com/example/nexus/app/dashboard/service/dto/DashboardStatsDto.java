package com.example.nexus.app.dashboard.service.dto;

public record DashboardStatsDto(
        Long totalLikes,
        Long yesterdayLikes,
        Long totalPendingApplications,
        Long yesterdayPendingApplications,
        Long totalApprovedParticipants,
        Long yesterdayApprovedParticipants,
        Long totalReviews,
        Long yesterdayReviews,
        Long totalPendingRewards,
        Long yesterdayPendingRewards
) {
}
