package com.example.nexus.app.datacenter.repository;

import com.example.nexus.app.datacenter.domain.ParticipantFeedback;

import java.time.LocalDateTime;
import java.util.List;

public interface ParticipantFeedbackRepositoryCustom {
    
    List<ParticipantFeedback> findByPostIdAndDateRange(
            Long postId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    Long countByPostId(Long postId);

    Long countByPostIdAndDateRange(
            Long postId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    Long countBugReportsByPostId(Long postId);

    Long countPositiveFeedbackByPostId(Long postId);

    Double getAverageOverallSatisfaction(Long postId);

    Double getAverageRecommendationIntent(Long postId);

    Double getAverageReuseIntent(Long postId);
}

