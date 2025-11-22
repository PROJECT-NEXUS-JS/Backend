package com.example.nexus.app.datacenter.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 피드백 통계 캐시 엔티티
 * - 주기적으로 집계된 통계 데이터를 캐싱하여 성능 최적화
 * - 스케줄러가 1시간마다 업데이트
 */
@Entity
@Table(name = "feedback_statistics_cache", indexes = {
    @Index(name = "idx_feedback_stats_post_id", columnList = "post_id"),
    @Index(name = "idx_feedback_stats_period", columnList = "post_id,period_days")
})
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FeedbackStatisticsCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "period_days", nullable = false)
    private Integer periodDays; // 7, 30, 90일

    // 요약 통계
    @Column(name = "total_participants")
    private Long totalParticipants;

    @Column(name = "average_satisfaction")
    private Double averageSatisfaction;

    @Column(name = "bug_occurrence_rate")
    private Double bugOccurrenceRate;

    @Column(name = "positive_feedback_rate")
    private Double positiveFeedbackRate;

    // 전반 평가
    @Column(name = "average_recommendation")
    private Double averageRecommendation;

    @Column(name = "average_reuse")
    private Double averageReuse;

    // 사용성 점수
    @Column(name = "functionality_score")
    private Double functionalityScore;

    @Column(name = "comprehensibility_score")
    private Double comprehensibilityScore;

    @Column(name = "loading_speed_score")
    private Double loadingSpeedScore;

    @Column(name = "response_timing_score")
    private Double responseTimingScore;

    @Column(name = "stability_score")
    private Double stabilityScore;

    // 전체 통계 JSON (선택적)
    @Column(name = "full_statistics_json", columnDefinition = "TEXT")
    private String fullStatisticsJson;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public FeedbackStatisticsCache(Long postId, Integer periodDays,
                                   Long totalParticipants, Double averageSatisfaction,
                                   Double bugOccurrenceRate, Double positiveFeedbackRate,
                                   Double averageRecommendation, Double averageReuse,
                                   Double functionalityScore, Double comprehensibilityScore,
                                   Double loadingSpeedScore, Double responseTimingScore,
                                   Double stabilityScore, String fullStatisticsJson) {
        this.postId = postId;
        this.periodDays = periodDays;
        this.totalParticipants = totalParticipants;
        this.averageSatisfaction = averageSatisfaction;
        this.bugOccurrenceRate = bugOccurrenceRate;
        this.positiveFeedbackRate = positiveFeedbackRate;
        this.averageRecommendation = averageRecommendation;
        this.averageReuse = averageReuse;
        this.functionalityScore = functionalityScore;
        this.comprehensibilityScore = comprehensibilityScore;
        this.loadingSpeedScore = loadingSpeedScore;
        this.responseTimingScore = responseTimingScore;
        this.stabilityScore = stabilityScore;
        this.fullStatisticsJson = fullStatisticsJson;
    }

    public void updateStatistics(Long totalParticipants, Double averageSatisfaction,
                                Double bugOccurrenceRate, Double positiveFeedbackRate,
                                Double averageRecommendation, Double averageReuse,
                                Double functionalityScore, Double comprehensibilityScore,
                                Double loadingSpeedScore, Double responseTimingScore,
                                Double stabilityScore, String fullStatisticsJson) {
        this.totalParticipants = totalParticipants;
        this.averageSatisfaction = averageSatisfaction;
        this.bugOccurrenceRate = bugOccurrenceRate;
        this.positiveFeedbackRate = positiveFeedbackRate;
        this.averageRecommendation = averageRecommendation;
        this.averageReuse = averageReuse;
        this.functionalityScore = functionalityScore;
        this.comprehensibilityScore = comprehensibilityScore;
        this.loadingSpeedScore = loadingSpeedScore;
        this.responseTimingScore = responseTimingScore;
        this.stabilityScore = stabilityScore;
        this.fullStatisticsJson = fullStatisticsJson;
    }
}

