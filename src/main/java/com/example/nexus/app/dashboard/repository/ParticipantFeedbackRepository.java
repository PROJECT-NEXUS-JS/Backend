package com.example.nexus.app.dashboard.repository;

import com.example.nexus.app.dashboard.domain.ParticipantFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipantFeedbackRepository extends JpaRepository<ParticipantFeedback, Long> {

    Optional<ParticipantFeedback> findByParticipationId(Long participationId);

    List<ParticipantFeedback> findByPostId(Long postId);

    @Query("SELECT pf FROM ParticipantFeedback pf WHERE pf.postId = :postId " +
           "AND pf.createdAt >= :startDate AND pf.createdAt < :endDate")
    List<ParticipantFeedback> findByPostIdAndDateRange(
            @Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(pf) FROM ParticipantFeedback pf WHERE pf.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(pf) FROM ParticipantFeedback pf WHERE pf.postId = :postId " +
           "AND pf.createdAt >= :startDate AND pf.createdAt < :endDate")
    Long countByPostIdAndDateRange(
            @Param("postId") Long postId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(pf) FROM ParticipantFeedback pf WHERE pf.postId = :postId AND pf.hasBug = true")
    Long countBugReportsByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(pf) FROM ParticipantFeedback pf WHERE pf.postId = :postId " +
           "AND pf.overallSatisfaction >= 4")
    Long countPositiveFeedbackByPostId(@Param("postId") Long postId);

    @Query("SELECT AVG(pf.overallSatisfaction) FROM ParticipantFeedback pf WHERE pf.postId = :postId")
    Double getAverageOverallSatisfaction(@Param("postId") Long postId);

    @Query("SELECT AVG(pf.recommendationIntent) FROM ParticipantFeedback pf WHERE pf.postId = :postId")
    Double getAverageRecommendationIntent(@Param("postId") Long postId);

    @Query("SELECT AVG(pf.reuseIntent) FROM ParticipantFeedback pf WHERE pf.postId = :postId")
    Double getAverageReuseIntent(@Param("postId") Long postId);
}

