package com.example.nexus.app.badge.dto;

import com.example.nexus.app.badge.domain.Badge;
import com.example.nexus.app.badge.domain.BadgeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeStatisticsDto {
    private Long badgeId;
    private String badgeName;
    private String description;
    private BadgeType badgeType;
    private Long acquiredCount;

    public static BadgeStatisticsDto of(Badge badge, Long count) {
        return BadgeStatisticsDto.builder()
                .badgeId(badge.getId())
                .badgeName(badge.getName())
                .description(badge.getDescription())
                .badgeType(badge.getBadgeType())
                .acquiredCount(count)
                .build();
    }
}


