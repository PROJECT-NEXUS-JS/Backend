package com.example.nexus.app.badge.dto;

import com.example.nexus.app.badge.domain.BadgeName;
import com.example.nexus.app.badge.domain.BadgeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BadgeStatisticsDto {

    private Long badgeId;
    private String badgeName;
    private String description;
    private BadgeType badgeType;
    private Long acquiredCount;

    /**
     * JPQL용 생성자
     */
    public BadgeStatisticsDto(Long badgeId, BadgeName badgeName, Long acquiredCount) {
        this.badgeId = badgeId;
        this.badgeName = badgeName.getDisplayName();
        this.description = badgeName.getDescription();
        this.badgeType = badgeName.getBadgeType();
        this.acquiredCount = acquiredCount;
    }
}
