package com.example.nexus.app.badge.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 뱃지 엔티티
 */
@Entity
@Table(name = "badges")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_name", nullable = false, unique = true, length = 50)
    private BadgeName badgeName;

    @Column(name = "icon_url", length = 500)
    private String iconUrl;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Badge(BadgeName badgeName, String iconUrl) {
        this.badgeName = badgeName;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return badgeName.getDisplayName();
    }

    public String getDescription() {
        return badgeName.getDescription();
    }

    public BadgeType getBadgeType() {
        return badgeName.getBadgeType();
    }

    public Integer getConditionValue() {
        return badgeName.getConditionValue();
    }

    public BadgeConditionType getConditionType() {
        return badgeName.getConditionType();
    }
}
