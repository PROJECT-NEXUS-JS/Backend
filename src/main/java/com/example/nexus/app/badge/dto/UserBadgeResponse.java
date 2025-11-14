package com.example.nexus.app.badge.dto;

import com.example.nexus.app.badge.domain.BadgeType;
import com.example.nexus.app.badge.domain.UserBadge;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 사용자 뱃지 응답 DTO
 */
@Getter
@Builder
public class UserBadgeResponse {
    private Long badgeId;
    private String badgeName;
    private String description;
    private String iconUrl;
    private BadgeType badgeType;
    private LocalDateTime acquiredAt;

    public static UserBadgeResponse from(UserBadge userBadge) {
        return UserBadgeResponse.builder()
                .badgeId(userBadge.getBadge().getId())
                .badgeName(userBadge.getBadge().getName())
                .description(userBadge.getBadge().getDescription())
                .iconUrl(userBadge.getBadge().getIconUrl())
                .badgeType(userBadge.getBadge().getBadgeType())
                .acquiredAt(userBadge.getAcquiredAt())
                .build();
    }
}

