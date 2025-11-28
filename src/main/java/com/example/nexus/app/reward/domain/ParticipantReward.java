package com.example.nexus.app.reward.domain;

import com.example.nexus.app.participation.domain.Participation;
import com.example.nexus.app.participation.domain.ParticipationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "participant_rewards",
        uniqueConstraints = @UniqueConstraint(columnNames = {"participation_id", "post_reward_id"}))
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ParticipantReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false)
    private Participation participation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_reward_id", nullable = false)
    private PostReward postReward;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_status")
    private ParticipationStatus completionStatus = ParticipationStatus.APPROVED;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_status")
    private RewardStatus rewardStatus = RewardStatus.PENDING;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static ParticipantReward create(Participation participation, PostReward postReward) {
        ParticipantReward reward = new ParticipantReward();
        reward.participation = participation;
        reward.postReward = postReward;
        reward.completionStatus = ParticipationStatus.APPROVED;
        return reward;
    }

    public void markAsCompleted() {
        this.completionStatus = ParticipationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.rewardStatus = RewardStatus.PENDING;
    }

    public void markAsPaid() {
        this.rewardStatus = RewardStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return this.completionStatus == ParticipationStatus.COMPLETED;
    }

    public boolean isRewardPaid() {
        return this.rewardStatus == RewardStatus.PAID;
    }
}
