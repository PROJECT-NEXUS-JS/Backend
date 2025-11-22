package com.example.nexus.app.datacenter.domain;

import com.example.nexus.app.participation.domain.Participation;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 참여자 피드백 응답 엔티티
 * - 참여자가 작성한 실제 피드백 데이터 저장
 * - 만족도, 추천의향, 재이용의향, 버그정보, 사용성 평가 등
 */
@Entity
@Table(name = "participant_feedbacks", indexes = {
    @Index(name = "idx_participant_feedbacks_post_id", columnList = "post_id"),
    @Index(name = "idx_participant_feedbacks_participation_id", columnList = "participation_id"),
    @Index(name = "idx_participant_feedbacks_created_at", columnList = "created_at")
})
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ParticipantFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private Participation participation;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    // 전반 평가
    @Column(name = "overall_satisfaction")
    private Integer overallSatisfaction; // 1-5점

    @Column(name = "recommendation_intent")
    private Integer recommendationIntent; // 1-5점

    @Column(name = "reuse_intent")
    private Integer reuseIntent; // 1-5점

    // 품질 피드백
    @Column(name = "has_bug")
    private Boolean hasBug;

    @Column(name = "bug_description", columnDefinition = "TEXT")
    private String bugDescription;

    @ElementCollection
    @CollectionTable(name = "feedback_inconvenient_elements", 
                    joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "element")
    private List<String> inconvenientElements = new ArrayList<>(); // UI/UX, 기능오류, 텍스트 등

    @ElementCollection
    @CollectionTable(name = "feedback_problem_types", 
                    joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "problem_type")
    private List<String> problemTypes = new ArrayList<>(); // 기능작동오류, 데이터입력오류, UI오류 등

    @Column(name = "problem_location")
    private String problemLocation; // 문제 발생 위치

    @ElementCollection
    @CollectionTable(name = "feedback_screenshots", 
                    joinColumns = @JoinColumn(name = "feedback_id"))
    @Column(name = "screenshot_url")
    private List<String> screenshotUrls = new ArrayList<>();

    // 기능별 사용성 평가 (1-5점)
    @Column(name = "functionality_score")
    private Integer functionalityScore; // 기능 작동성

    @Column(name = "comprehensibility_score")
    private Integer comprehensibilityScore; // 이해도

    @Column(name = "loading_speed_score")
    private Integer loadingSpeedScore; // 로딩 속도

    @Column(name = "response_timing_score")
    private Integer responseTimingScore; // 반응 타이밍

    @Column(name = "stability_score")
    private Integer stabilityScore; // 안정성

    // 개선 제안 및 긍정 피드백
    @Column(name = "positive_feedback", columnDefinition = "TEXT")
    private String positiveFeedback; // 좋았던 점

    @Column(name = "improvement_suggestion", columnDefinition = "TEXT")
    private String improvementSuggestion; // 개선 제안

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public ParticipantFeedback(Participation participation, Long postId,
                              Integer overallSatisfaction, Integer recommendationIntent, Integer reuseIntent,
                              Boolean hasBug, String bugDescription,
                              List<String> inconvenientElements, List<String> problemTypes, String problemLocation,
                              List<String> screenshotUrls,
                              Integer functionalityScore, Integer comprehensibilityScore,
                              Integer loadingSpeedScore, Integer responseTimingScore, Integer stabilityScore,
                              String positiveFeedback, String improvementSuggestion) {
        this.participation = participation;
        this.postId = postId;
        this.overallSatisfaction = overallSatisfaction;
        this.recommendationIntent = recommendationIntent;
        this.reuseIntent = reuseIntent;
        this.hasBug = hasBug;
        this.bugDescription = bugDescription;
        if (inconvenientElements != null) {
            this.inconvenientElements.addAll(inconvenientElements);
        }
        if (problemTypes != null) {
            this.problemTypes.addAll(problemTypes);
        }
        this.problemLocation = problemLocation;
        if (screenshotUrls != null) {
            this.screenshotUrls.addAll(screenshotUrls);
        }
        this.functionalityScore = functionalityScore;
        this.comprehensibilityScore = comprehensibilityScore;
        this.loadingSpeedScore = loadingSpeedScore;
        this.responseTimingScore = responseTimingScore;
        this.stabilityScore = stabilityScore;
        this.positiveFeedback = positiveFeedback;
        this.improvementSuggestion = improvementSuggestion;
    }

    public void update(Integer overallSatisfaction, Integer recommendationIntent, Integer reuseIntent,
                      Boolean hasBug, String bugDescription,
                      List<String> inconvenientElements, List<String> problemTypes, String problemLocation,
                      List<String> screenshotUrls,
                      Integer functionalityScore, Integer comprehensibilityScore,
                      Integer loadingSpeedScore, Integer responseTimingScore, Integer stabilityScore,
                      String positiveFeedback, String improvementSuggestion) {
        this.overallSatisfaction = overallSatisfaction;
        this.recommendationIntent = recommendationIntent;
        this.reuseIntent = reuseIntent;
        this.hasBug = hasBug;
        this.bugDescription = bugDescription;
        
        this.inconvenientElements.clear();
        if (inconvenientElements != null) {
            this.inconvenientElements.addAll(inconvenientElements);
        }
        
        this.problemTypes.clear();
        if (problemTypes != null) {
            this.problemTypes.addAll(problemTypes);
        }
        
        this.problemLocation = problemLocation;
        
        this.screenshotUrls.clear();
        if (screenshotUrls != null) {
            this.screenshotUrls.addAll(screenshotUrls);
        }
        
        this.functionalityScore = functionalityScore;
        this.comprehensibilityScore = comprehensibilityScore;
        this.loadingSpeedScore = loadingSpeedScore;
        this.responseTimingScore = responseTimingScore;
        this.stabilityScore = stabilityScore;
        this.positiveFeedback = positiveFeedback;
        this.improvementSuggestion = improvementSuggestion;
    }
}

