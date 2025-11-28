package com.example.nexus.app.participation.service.dto;

public record ParticipationStatsDto(
        Long pendingCount,
        Long approvedCount,
        Long completedCount,
        Long paidCount,
        Long rejectedCount
) {
}
