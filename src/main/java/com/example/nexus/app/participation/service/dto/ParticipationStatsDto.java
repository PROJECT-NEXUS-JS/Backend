package com.example.nexus.app.participation.service.dto;

public record ParticipationStatsDto(
        Long pendingCount,
        Long approvedCount,
        Long feedbackCompletedCount,
        Long testCompletedCount,
        Long paidCount,
        Long rejectedCount
) {
}
