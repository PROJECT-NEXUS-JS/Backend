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
 * 피드백 임시저장 엔티티
 */
@Entity
@Table(name = "feedback_drafts", indexes = {
        @Index(name = "idx_feedback_draft_participation_id", columnList = "participation_id"),
        @Index(name = "idx_feedback_draft_updated_at", columnList = "updated_at")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackDraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false, unique = true)
    private Participation participation;

    @Column(name = "overall_satisfaction")
    private Integer overallSatisfaction;

    @Column(name = "recommendation_intent")
    private Integer recommendationIntent;

    @Column(name = "reuse_intent")
    private Integer reuseIntent;

    @Enumerated(EnumType.STRING)
    @Column(name = "most_inconvenient", length = 20)
    private InconvenienceType mostInconvenient;

    @Column(name = "has_bug")
    private Boolean hasBug;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "feedback_draft_bug_types", joinColumns = @JoinColumn(name = "feedback_draft_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "bug_type", length = 30)
    private Set<BugType> bugTypes = new HashSet<>();

    @Column(name = "bug_location", length = 500)
    private String bugLocation;

    @Column(name = "bug_description", columnDefinition = "TEXT")
    private String bugDescription;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "feedback_draft_screenshots", joinColumns = @JoinColumn(name = "feedback_draft_id"))
    @Column(name = "screenshot_url", length = 500)
    private List<String> screenshotUrls = new ArrayList<>();

    @Column(name = "functionality_score")
    private Integer functionalityScore;

    @Column(name = "comprehensibility_score")
    private Integer comprehensibilityScore;

    @Column(name = "speed_score")
    private Integer speedScore;

    @Column(name = "response_timing_score")
    private Integer responseTimingScore;

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
    public FeedbackDraft(Participation participation,
                         Integer overallSatisfaction, Integer recommendationIntent, Integer reuseIntent,
                         InconvenienceType mostInconvenient,
                         Boolean hasBug, Set<BugType> bugTypes, String bugLocation, String bugDescription,
                         List<String> screenshotUrls,
                         Integer functionalityScore, Integer comprehensibilityScore,
                         Integer speedScore, Integer responseTimingScore,
                         String goodPoints, String improvementSuggestions, String additionalComments) {

        this.participation = participation;
        this.overallSatisfaction = overallSatisfaction;
        this.recommendationIntent = recommendationIntent;
        this.reuseIntent = reuseIntent;
        this.mostInconvenient = mostInconvenient;
        this.hasBug = hasBug;
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

    public void update(Integer overallSatisfaction, Integer recommendationIntent, Integer reuseIntent,
                       InconvenienceType mostInconvenient,
                       Boolean hasBug, Set<BugType> bugTypes, String bugLocation, String bugDescription,
                       List<String> screenshotUrls,
                       Integer functionalityScore, Integer comprehensibilityScore,
                       Integer speedScore, Integer responseTimingScore,
                       String goodPoints, String improvementSuggestions, String additionalComments) {

        this.overallSatisfaction = overallSatisfaction;
        this.recommendationIntent = recommendationIntent;
        this.reuseIntent = reuseIntent;
        this.mostInconvenient = mostInconvenient;
        this.hasBug = hasBug;

        this.bugTypes.clear();
        if (bugTypes != null) {
            this.bugTypes.addAll(bugTypes);
        }

        this.bugLocation = bugLocation;
        this.bugDescription = bugDescription;

        this.screenshotUrls.clear();
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

    public Feedback toFeedback() {
        return Feedback.builder()
                .participation(participation)
                .overallSatisfaction(overallSatisfaction)
                .recommendationIntent(recommendationIntent)
                .reuseIntent(reuseIntent)
                .mostInconvenient(mostInconvenient)
                .hasBug(hasBug)
                .bugTypes(new HashSet<>(bugTypes))
                .bugLocation(bugLocation)
                .bugDescription(bugDescription)
                .screenshotUrls(new ArrayList<>(screenshotUrls))
                .functionalityScore(functionalityScore)
                .comprehensibilityScore(comprehensibilityScore)
                .speedScore(speedScore)
                .responseTimingScore(responseTimingScore)
                .goodPoints(goodPoints)
                .improvementSuggestions(improvementSuggestions)
                .additionalComments(additionalComments)
                .build();
    }
}

