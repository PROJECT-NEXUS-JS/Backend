package com.example.nexus.app.dashboard.service.dto;

public record PieChartStatsDto(
        Long pendingCount,
        Long approvedCount,
        Long completedCount,
        Long pendingRewards,
        Long paidRewards
) {
}
