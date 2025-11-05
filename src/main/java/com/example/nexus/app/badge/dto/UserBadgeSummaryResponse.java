package com.example.nexus.app.badge.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 사용자 뱃지 요약 응답 DTO
 */
@Getter
@Builder
public class UserBadgeSummaryResponse {
    private Long userId;
    private Long totalBadgeCount;
    private List<UserBadgeResponse> badges;

    public static UserBadgeSummaryResponse of(Long userId, Long totalBadgeCount, List<UserBadgeResponse> badges) {
        return UserBadgeSummaryResponse.builder()
                .userId(userId)
                .totalBadgeCount(totalBadgeCount)
                .badges(badges)
                .build();
    }
}

