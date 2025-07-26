package com.example.nexus.app.post.domain;

import com.example.nexus.app.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "participations", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "post_id"}))
@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipationStatus status;

    @Builder
    public Participation(Post post, User user, ParticipationStatus status) {
        this.post = post;
        this.user = user;
        this.status = status;
    }

    public void approve() {
        this.status = ParticipationStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = ParticipationStatus.REJECTED;
        this.approvedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = ParticipationStatus.COMPLETED;
    }

    public boolean isPending() {
        return this.status == ParticipationStatus.PENDING;
    }

    public boolean isApproved() {
        return this.status == ParticipationStatus.APPROVED;
    }

    public boolean isRejected() {
        return this.status == ParticipationStatus.REJECTED;
    }

    public boolean isCompleted() {
        return this.status == ParticipationStatus.COMPLETED;
    }
}
