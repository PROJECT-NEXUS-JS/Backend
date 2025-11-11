package com.example.nexus.app.dashboard.repository;

import com.example.nexus.app.dashboard.domain.FeedbackStatisticsCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeedbackStatisticsCacheRepository extends JpaRepository<FeedbackStatisticsCache, Long> {

    Optional<FeedbackStatisticsCache> findByPostIdAndPeriodDays(Long postId, Integer periodDays);

    void deleteByPostId(Long postId);
}

