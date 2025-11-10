package com.example.nexus.app.feedback.domain;

import com.example.nexus.app.participation.domain.Participation;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 피드백 엔티티
 * - 참여자가 베타 테스트 후 제출하는 상세 평가/피드백
 * - 1인 1회 제출 (participation_id unique)
 */
@Entity
@Table(name = "feedbacks", indexes = {
        @Index(name = "idx_feedback_participation_id", columnList = "participation_id"),
        @Index(name = "idx_feedback_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false, unique = true)
    private Participation participation;

    // ============ 전반 평가 ============
    @Column(name = "overall_satisfaction", nullable = false)
    private Integer overallSatisfaction;

    @Column(name = "recommendation_intent", nullable = false)
    private Integer recommendationIntent;

    @Column(name = "reuse_intent", nullable = false)
    private Integer reuseIntent;

    // ============ 테스트 품질 관련 피드백 ============
    @Enumerated(EnumType.STRING)
    @Column(name = "most_inconvenient", length = 20)
    private InconvenienceType mostInconvenient;

    @Column(name = "has_bug", nullable = false)
    private Boolean hasBug;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "feedback_bug_types", joinColumns = @JoinColumn(name = "feedback_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "bug_type", length = 30)
    private Set<BugType> bugTypes = new HashSet<>();

    @Column(name = "bug_location", length = 500)
    private String bugLocation;

    @Column(name = "bug_description", columnDefinition = "TEXT")
    private String bugDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "feedback_screenshots", joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "screenshot_url", length = 500)
    private List<String> screenshotUrls = new ArrayList<>();

    // ============ 기능별 사용성 평가 ============
    @Column(name = "functionality_score", nullable = false)
    private Integer functionalityScore;

    @Column(name = "comprehensibility_score", nullable = false)
    private Integer comprehensibilityScore;

    @Column(name = "speed_score", nullable = false)
    private Integer speedScore;

    @Column(name = "response_timing_score", nullable = false)
    private Integer responseTimingScore;

    // ============ 개선 제안 및 인사이트 ============
    @Column(name = "good_points", columnDefinition = "TEXT")
    private String goodPoints;

    @Column(name = "improvement_suggestions", columnDefinition = "TEXT")
    private String improvementSuggestions;

    @Column(name = "additional_comments", columnDefinition = "TEXT")
    private String additionalComments;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Feedback(Participation participation,
                    Integer overallSatisfaction, Integer recommendationIntent, Integer reuseIntent,
                    InconvenienceType mostInconvenient,
                    Boolean hasBug, Set<BugType> bugTypes, String bugLocation, String bugDescription,
                    List<String> screenshotUrls,
                    Integer functionalityScore, Integer comprehensibilityScore,
                    Integer speedScore, Integer responseTimingScore,
                    String goodPoints, String improvementSuggestions, String additionalComments) {

        validateRating(overallSatisfaction, "전반적인 만족도");
        validateRating(recommendationIntent, "추천 의향");
        validateRating(reuseIntent, "재이용 의향");
        validateRating(functionalityScore, "기능 작동성");
        validateRating(comprehensibilityScore, "가이드 이해도");
        validateRating(speedScore, "로딩 속도");
        validateRating(responseTimingScore, "반응 타이밍");

        this.participation = participation;
        this.overallSatisfaction = overallSatisfaction;
        this.recommendationIntent = recommendationIntent;
        this.reuseIntent = reuseIntent;
        this.mostInconvenient = mostInconvenient;
        this.hasBug = hasBug != null ? hasBug : false;
        if (bugTypes != null) {
            this.bugTypes.addAll(bugTypes);
        }
        this.bugLocation = bugLocation;
        this.bugDescription = bugDescription;
        if (screenshotUrls != null) {
            this.screenshotUrls.addAll(screenshotUrls);
        }
        this.functionalityScore = functionalityScore;
        this.comprehensibilityScore = comprehensibilityScore;
        this.speedScore = speedScore;
        this.responseTimingScore = responseTimingScore;
        this.goodPoints = goodPoints;
        this.improvementSuggestions = improvementSuggestions;
        this.additionalComments = additionalComments;
    }

    private void validateRating(Integer rating, String fieldName) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException(fieldName + "는 1에서 5 사이의 값이어야 합니다.");
        }
    }

    public Double getAverageSatisfaction() {
        return (overallSatisfaction + recommendationIntent + reuseIntent) / 3.0;
    }

    public Double getAverageUsabilityScore() {
        return (functionalityScore + comprehensibilityScore + speedScore + responseTimingScore) / 4.0;
    }
}

