package com.example.nexus.app.badge.dto;

import com.example.nexus.app.badge.domain.Badge;
import com.example.nexus.app.badge.domain.BadgeType;
import lombok.Builder;
import lombok.Getter;

/**
 * 뱃지 응답 DTO
 */
@Getter
@Builder
public class BadgeResponse {
    private Long badgeId;
    private String badgeName;
    private String description;
    private String iconUrl;
    private BadgeType badgeType;
    private Integer conditionValue;

    public static BadgeResponse from(Badge badge) {
        return BadgeResponse.builder()
                .badgeId(badge.getId())
                .badgeName(badge.getName())
                .description(badge.getDescription())
                .iconUrl(badge.getIconUrl())
                .badgeType(badge.getBadgeType())
                .conditionValue(badge.getConditionValue())
                .build();
    }
}

