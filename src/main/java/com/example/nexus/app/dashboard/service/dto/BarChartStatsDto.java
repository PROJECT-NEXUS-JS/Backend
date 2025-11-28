package com.example.nexus.app.dashboard.service.dto;

public record BarChartStatsDto(
        Long totalLikes,
        Long totalPendingApplications,
        Long totalApprovedParticipants,
        Long totalReviews
) {
}
