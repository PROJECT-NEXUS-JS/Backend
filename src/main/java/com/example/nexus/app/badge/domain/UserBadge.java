package com.example.nexus.app.badge.domain;

import com.example.nexus.app.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자-뱃지 매핑 엔티티
 */
@Entity
@Table(
    name = "user_badges",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_badge", columnNames = {"user_id", "badge_id"})
    },
    indexes = {
        @Index(name = "idx_user_badges_user_id", columnList = "user_id"),
        @Index(name = "idx_user_badges_badge_id", columnList = "badge_id")
    }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id", nullable = false)
    private Badge badge;

    @CreatedDate
    @Column(name = "acquired_at", nullable = false, updatable = false)
    private LocalDateTime acquiredAt;

    @Builder
    public UserBadge(User user, Badge badge) {
        validateUserBadge(user, badge);
        this.user = user;
        this.badge = badge;
    }

    private void validateUserBadge(User user, Badge badge) {
        if (user == null) {
            throw new IllegalArgumentException("User는 필수입니다.");
        }
        if (badge == null) {
            throw new IllegalArgumentException("Badge는 필수입니다.");
        }
    }
}
