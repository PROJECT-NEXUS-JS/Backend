package com.example.nexus.app.dashboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_post_statistics")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DailyPostStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate;

    @Column(name = "likes_count", nullable = false)
    private Long likesCount = 0L;

    @Column(name = "pending_applications_count", nullable = false)
    private Long pendingApplicationsCount = 0L;

    @Column(name = "approved_participants_count", nullable = false)
    private Long approvedParticipantsCount = 0L;

    @Column(name = "completed_participants_count", nullable = false)
    private Long completedParticipantsCount = 0L;

    @Column(name = "reviews_count", nullable = false)
    private Long reviewsCount = 0L;

    @Column(name = "views_count", nullable = false)
    private Long viewsCount = 0L;

    @Column(name = "unread_messages_count", nullable = false)
    private Long unreadMessagesCount = 0L;

    @Column(name = "pending_rewards_count", nullable = false)
    private Long pendingRewardsCount = 0L;

    @Column(name = "paid_rewards_count", nullable = false)
    private Long paidRewardsCount = 0L;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static DailyPostStatistics create(Long postId, LocalDate statDate, Long likes, Long pending,
                                            Long approved, Long completed, Long reviews, Long views, Long unreadMessages,
                                            Long pendingRewards, Long paidRewards) {
        DailyPostStatistics stats = new DailyPostStatistics();
        stats.postId = postId;
        stats.statDate = statDate;
        stats.likesCount = likes;
        stats.pendingApplicationsCount = pending;
        stats.approvedParticipantsCount = approved;
        stats.completedParticipantsCount = completed;
        stats.reviewsCount = reviews;
        stats.viewsCount = views;
        stats.unreadMessagesCount = unreadMessages;
        stats.pendingRewardsCount = pendingRewards;
        stats.paidRewardsCount = paidRewards;
        return stats;
    }
}
