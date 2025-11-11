package com.example.nexus.app.post.controller.dto.request;

import com.example.nexus.app.reward.domain.RewardType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PostSearchRequest(
        @Schema(description = "검색 키워드")
        String keyword,

        @Schema(description = "메인 카테고리")
        String mainCategory,

        @Schema(description = "플랫폼 카테고리")
        String platformCategory,

        @Schema(description = "리워드 타입")
        RewardType rewardType,

        @Schema(description = "정렬 기준 (latest, popular, deadline)")
        String sortBy
) {

    public static PostSearchRequest of(String keyword, String mainCategory, String platformCategory,
                                       RewardType rewardType, String sortBy) {
        return new PostSearchRequest(keyword, mainCategory, platformCategory, rewardType, sortBy);
    }
}
